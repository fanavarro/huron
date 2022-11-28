package tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import metrics.Metric;

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
	 */
	public MetricCalculationTask(List<Metric> metric, File ontologyFile, boolean includeDetailFiles) {
		super();
		this.metrics = metric;
		this.ontologyFile = ontologyFile;
		this.includeDetailFiles = includeDetailFiles;
		this.detailedFileFolder = null;
		if(this.includeDetailFiles){
			this.detailedFileFolder = new File(DETAIL_FILES_FOLDER);
			if(!this.detailedFileFolder.exists() || !this.detailedFileFolder.isDirectory()){
				try {
					Files.createDirectories(this.detailedFileFolder.toPath());
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
	 * @param detailedFilesFolder the folder where the detailed file will be saved.
	 */
	public MetricCalculationTask(List<Metric> metric, File ontologyFile, boolean includeDetailFiles, File detailsFileFolder) {
		super();
		this.metrics = metric;
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

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<MetricCalculationTaskResult> call() throws Exception {
		List<MetricCalculationTaskResult> results = new ArrayList<MetricCalculationTaskResult>();
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		LOGGER.log(Level.INFO, String.format("Loading %s", ontologyFile.getName()));
		ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
		LOGGER.log(Level.INFO, String.format(" %s loaded", ontologyFile.getName()));
		for (Metric metric : metrics) {
			LOGGER.log(Level.INFO, String.format("%s for %s\t-Calculating", metric.getName(), ontologyFile.getName()));
			metric.setOntology(ontology);
			String detailedFileName = ontologyFile.getName() + "_" + metric.getName().replace(' ', '_') + ".tsv";
			if(this.includeDetailFiles){
				metric.openDetailedOutputFile(new File(this.detailedFileFolder, detailedFileName));
			}
			double result = metric.calculate();
			if(this.includeDetailFiles){
				metric.closeDetailedOutputFile();
			}
			results.add(new MetricCalculationTaskResult(metric.getName(), result, ontologyFile.getName()));
			LOGGER.log(Level.INFO, String.format("%s for %s\t-Done", metric.getName(), ontologyFile.getName()));
		}
		ontologyManager.removeOntology(ontology);
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
