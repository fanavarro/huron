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

import metrics.DetailedOutputHeaderMetricInterface;
import metrics.Metric;
import um.ontoenrich.config.LaInputParameters;

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
	
	/** The parameters. */
	private LaInputParameters parameters;
	
	/** The include detail files. */
	private boolean includeDetailFiles;

	/**
	 * Instantiates a new metric calculation task.
	 *
	 * @param metric the metric
	 * @param ontologyFile the ontology file
	 * @param parameters the parameters
	 * @param includeDetailFiles the include detail files
	 */
	public MetricCalculationTask(List<Metric> metric, File ontologyFile, LaInputParameters parameters, boolean includeDetailFiles) {
		super();
		this.metrics = metric;
		this.ontologyFile = ontologyFile;
		this.parameters = parameters;
		this.includeDetailFiles = includeDetailFiles;	
		
		if(this.includeDetailFiles){
			File detailsFileFolder = new File(DETAIL_FILES_FOLDER);
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
	 * Load the ontology from a file name and set it into the metric object. 
	 * Invoke calculate for each metric.
	 * Set the result into a new MetricCalculationTaskResult object.
	 * 
	 * @return A list of MetricCalculationTaskResult
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
			metric.setParameters(parameters);
			String detailedFileName = ontologyFile.getName() + "_" + metric.getName().replace(' ', '_') + ".tsv";
			if(this.includeDetailFiles){
				metric.openDetailedOutputFile(new File(DETAIL_FILES_FOLDER + '/' + detailedFileName));
			}
			double result = metric.calculate();
			if(this.includeDetailFiles){
				metric.closeDetailedOutputFile();
			}
			
			if (metric instanceof DetailedOutputHeaderMetricInterface) {
				DetailedOutputHeaderMetricInterface m = (DetailedOutputHeaderMetricInterface) metric;
				results.add(new MetricCalculationDetailedTaskResult(metric.getName(), result, ontologyFile.getName(), m.getDividend(), m.getDivisor()));
			}else {
				results.add(new MetricCalculationTaskResult(metric.getName(), result, ontologyFile.getName()));
			}
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
