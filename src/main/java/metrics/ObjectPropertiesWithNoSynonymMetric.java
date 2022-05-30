package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

public class ObjectPropertiesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no synonym";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tObjectProperty\n");
		int numberOfObjectPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().getObjectPropertiesInSignature()){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlObjectProperty);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\n", this.getName(), owlObjectProperty.toStringID()));
				numberOfObjectPropertiesWithNoSynonym++;
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfObjectPropertiesWithNoSynonym;
		return ((double) (numberOfObjectPropertiesWithNoSynonym)) / numberOfEntities;		
	}

	@Override
	public void setParameters(LaInputParameters parameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return METRIC_NAME;
	}

	@Override
	public int getDivisor() {
		return totalEntities;
	}

	@Override
	public int getDividend() {
		return numberOfEntitiesWithNoAnnotation;
	}		

}
