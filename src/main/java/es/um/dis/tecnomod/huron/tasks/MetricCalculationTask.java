package es.um.dis.tecnomod.huron.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.metrics.Metric;
import es.um.dis.tecnomod.huron.utils.CatalogXmlIRIMapper;

/**
 * The Class MetricCalculationTask.
 */
public class MetricCalculationTask implements Callable<List<MetricCalculationTaskResult>> {
	
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
	

	

	
	
	/**
	 * Instantiates a new metric calculation task.
	 *
	 * @param metric the metric
	 * @param ontologyFile the ontology file
	 * @param parameters the parameters
	 * @param includeDetailFiles the include detail files
	 * @param detailedFilesFolder the folder where the detailed file will be saved.
	 */
	public MetricCalculationTask(List<Metric> metrics, File ontologyFile) {
		super();
		this.ontologyManager = OWLManager.createConcurrentOWLOntologyManager();
		/* Prevent exceptions when an import cannot be loaded*/
		this.ontologyManager.getOntologyConfigurator().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		/* Load catalog file for local file imports declaration */
		OWLOntologyIRIMapper iriMapper = this.getIRIMapper(ontologyFile.getAbsolutePath());
		if (iriMapper != null) {
			this.ontologyManager.getIRIMappers().add(iriMapper);
		}
		this.metrics = metrics;
		this.ontologyFile = ontologyFile;
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
			double result = Double.NaN;
			try {
				MetricResult metricResult = metric.calculate();
				result = metricResult.getMetricValue();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, String.format("Error calculating the '%s' metric", metric.getName()), e);
			}
			results.add(new MetricCalculationTaskResult(metric.getName(), result, ontologyFile.getName()));
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
	
	private OWLOntologyIRIMapper getIRIMapper(String ontologyPath) {
		File ontologyFile = new File(ontologyPath);
		File parentFolder = ontologyFile.getParentFile();
		Path catalogPath = Paths.get(parentFolder.getAbsolutePath(), "catalog-v001.xml");
		if (catalogPath.toFile().exists()) {
			try {
				return new CatalogXmlIRIMapper(catalogPath.toFile().getAbsolutePath());
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error loading catalog file.", e);
				return null;
			}
		}
		return null;
	}

}
