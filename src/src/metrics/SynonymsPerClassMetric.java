package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;
import um.ontoenrich.config.LaInputParameters;

/**
 * The Class SynonymsPerClassMetric.
 */
public class SynonymsPerClassMetric extends AnnotationsPerEntityAbstractMetric {
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Synonyms per class";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tMetric Value\n");
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().getClassesInSignature()){
			if (OntologyUtils.isObsolete(owlClass, getOntology())) {
				continue;
			}
			int localNumberOfSynonyms = getNumberOfSynonyms(owlClass);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlClass.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		return ((double) (numberOfSynonyms)) / numberOfEntities;
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#setParameters(um.ontoenrich.config.LaInputParameters)
	 */
	@Override
	public void setParameters(LaInputParameters parameters) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}

}
