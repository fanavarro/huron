package es.um.dis.tecnomod.huron.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.metrics.Metric;

/**
 * The Class MetricCalculationTask.
 */
public class MetricCalculationTask implements Callable<List<MetricCalculationTaskResult>> {
	
	/** The Constant DETAIL_FILES_FOLDER. */
	private static final String DETAIL_FILES_FOLDER = "detailed_files";
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(MetricCalculationTask.class.getName());
	
	/** The metrics. */
	private List<Metric> metrics;
	
	/** The ontology file. */
	private File ontologyFile;
	
	/** The ontology. */
	private OWLOntology ontology;
	
	/** The ontology manager */
	private OWLOntologyManager ontologyManager;
	
	/** The include detail files. */
	private boolean includeDetailFiles;
	
	private File detailedFileFolder;
	

	
	
	/**
	 * Instantiates a new metric calculation task.
	 *
	 * @param metric the metric
	 * @param ontologyFile the ontology file
	 * @param parameters the parameters
	 * @param includeDetailFiles the include detail files
	 * @param detailedFilesFolder the folder where the detailed file will be saved.
	 */
	public MetricCalculationTask(List<Metric> metrics, File ontologyFile, boolean includeDetailFiles, File detailsFileFolder) {
		super();
		this.ontologyManager = OWLManager.createConcurrentOWLOntologyManager();
		/* Prevent exceptions when an import cannot be loaded*/
		this.ontologyManager.getOntologyConfigurator().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		this.metrics = metrics;
		this.ontologyFile = ontologyFile;
		this.includeDetailFiles = includeDetailFiles;
		if(this.includeDetailFiles){
			this.detailedFileFolder = detailsFileFolder;
			if(!detailsFileFolder.exists() || !detailsFileFolder.isDirectory()){
				try {
					Files.createDirectories(detailsFileFolder.toPath());
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, "Error creating folder for detailed files. Ignoring detailed files...", e);
				}
			}
		}
	}
	
	/**
	 * Instantiates a new metric calculation task.
	 *
	 * @param metric the metric
	 * @param ontologyFile the ontology file
	 * @param parameters the parameters
	 * @param includeDetailFiles the include detail files
	 */
	public MetricCalculationTask(List<Metric> metrics, File ontologyFile, boolean includeDetailFiles) {
		this(metrics, ontologyFile, includeDetailFiles, new File(DETAIL_FILES_FOLDER));
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<MetricCalculationTaskResult> call() throws OWLOntologyCreationException, IOException, InterruptedException  {
		List<MetricCalculationTaskResult> results = new ArrayList<MetricCalculationTaskResult>();
		
		LOGGER.log(Level.INFO, String.format("Loading %s", ontologyFile.getName()));
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
		
		LOGGER.log(Level.INFO, String.format(" %s loaded", ontologyFile.getName()));
		for (Metric metric : metrics) {
			LOGGER.log(Level.INFO, String.format("%s for %s\t-Calculating", metric.getName(), ontologyFile.getName()));
			metric.setOntology(this.ontology);
			if(this.includeDetailFiles){
				String detailedFileName = this.ontologyFile.getName() + "_" + metric.getName().replace(' ', '_') + ".tsv";
				metric.openDetailedOutputFile(new File(this.detailedFileFolder, detailedFileName));
			}
			double result = Double.NaN;
			Model rdf = null;
			try {
				MetricResult metricResult = metric.calculate();
				result = metricResult.getMetricValue();
				rdf = metricResult.getRdf();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, String.format("Error calculating the '%s' metric", metric.getName()), e);
			}
			if(this.includeDetailFiles){
				metric.closeDetailedOutputFile();
			}
			results.add(new MetricCalculationTaskResult(metric.getName(), result, ontologyFile.getName(), rdf));
			LOGGER.log(Level.INFO, String.format("%s for %s\t-Done", metric.getName(), ontologyFile.getName()));
		}
		
		/* Release memory */
		ontologyManager.clearOntologies();
		ontologyManager.getOntologyStorers().clear();
		ontologyManager.getIRIMappers().clear();
		return results;
	}
	
	/**
	 * Gets the ontology file.
	 *
	 * @return the ontology file
	 */
	public File getOntologyFile(){
		return this.ontologyFile;
	}

}
