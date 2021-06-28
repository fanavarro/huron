package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

/**
 * The Class NumberOfClassesMetric.
 */
public class NumberOfClassesMetric extends Metric {
	
	/** The Constant NAME. */
	private static final String NAME = "Number of classes";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		return getOntology().getClassesInSignature().size();
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#setParameters(um.ontoenrich.config.LaInputParameters)
	 */
	@Override
	public void setParameters(LaInputParameters parameters) {
		// Not used
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

}
