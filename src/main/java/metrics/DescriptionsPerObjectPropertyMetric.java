package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;

/**
 * The Class DescriptionsPerObjectPropertyMetric.
 */
public class DescriptionsPerObjectPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Descriptions per object property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject Property\tMetric Value\n");
		int numberOfDescriptions = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty objectProperty : super.getOntology().getObjectPropertiesInSignature()){
			if (OntologyUtils.isObsolete(objectProperty, getOntology()) || objectProperty.isOWLTopObjectProperty()) {
				continue;
			}
			int localNumberOfdescriptions = getNumberOfDescriptions(objectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), objectProperty.toStringID(), localNumberOfdescriptions));
			numberOfDescriptions = numberOfDescriptions + localNumberOfdescriptions;
			numberOfEntities ++;
		}
		return ((double) (numberOfDescriptions)) / numberOfEntities;
	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}
}
