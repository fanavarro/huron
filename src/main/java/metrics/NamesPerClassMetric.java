package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;

/**
 * The Class NamesPerClassMetric.
 */
public class NamesPerClassMetric extends AnnotationsPerEntityAbstractMetric {
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Names per class";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tMetric Value\n");
		int numberOfNames = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().getClassesInSignature()){
			if (OntologyUtils.isObsolete(owlClass, getOntology()) || owlClass.isOWLThing()) {
				continue;
			}
			int localNumberOfNames = getNumberOfNames(owlClass);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlClass.toStringID(), localNumberOfNames));
			numberOfNames = numberOfNames + localNumberOfNames;
			numberOfEntities ++;
		}
		return ((double) (numberOfNames)) / numberOfEntities;
	}
	

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}

}