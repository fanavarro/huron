package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;

/**
 * The Class SynonymsPerDataPropertyMetric.
 */
public class SynonymsPerDataPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Synonyms per data property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tData Property\tMetric Value\n");
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty dataProperty : super.getOntology().getDataPropertiesInSignature()){
			if (OntologyUtils.isObsolete(dataProperty, getOntology()) || dataProperty.isOWLTopDataProperty()) {
				continue;
			}
			int localNumberOfSynonyms = getNumberOfSynonyms(dataProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), dataProperty.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		return ((double) (numberOfSynonyms)) / numberOfEntities;
	}



	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}
}


