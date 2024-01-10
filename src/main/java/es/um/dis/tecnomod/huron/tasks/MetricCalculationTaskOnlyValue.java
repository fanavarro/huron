package es.um.dis.tecnomod.huron.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.metrics.Metric;

public class MetricCalculationTaskOnlyValue extends MetricCalculationTask {
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(MetricCalculationTaskOnlyValue.class.getName());

	public MetricCalculationTaskOnlyValue(List<Metric> metrics, File ontologyFile, boolean includeDetailFiles) {
		super(metrics, ontologyFile, includeDetailFiles);
	}

	public MetricCalculationTaskOnlyValue(List<Metric> metrics, File ontologyFile, boolean includeDetailFiles,
			File detailsFileFolder) {
		super(metrics, ontologyFile, includeDetailFiles, detailsFileFolder);
	}
	
	@Override
	public List<MetricCalculationTaskResult> call() throws OWLOntologyCreationException, IOException, InterruptedException  {
		List<MetricCalculationTaskResult> results = super.call();
		this.cleanRDF(results);
		return results;
	}
	
	private void cleanRDF(List<MetricCalculationTaskResult> results) {
		for(MetricCalculationTaskResult result : results) {
			LOGGER.info("Removing RDF...");
			result.getRdf().removeAll();
			result.getRdf().close();
		}
	}

}
