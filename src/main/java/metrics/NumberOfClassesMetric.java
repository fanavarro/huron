package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;

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
		return getOntology().classesInSignature().filter(owlClass -> (!OntologyUtils.isObsolete(owlClass, getOntology()))).count();
	}



	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

}
