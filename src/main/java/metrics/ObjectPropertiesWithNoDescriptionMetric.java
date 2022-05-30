package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

public class ObjectPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no description";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tObjectProperty\n");
		int numberOfObjectPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().getObjectPropertiesInSignature()){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlObjectProperty);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\n", this.getName(), owlObjectProperty.toStringID()));
				numberOfObjectPropertiesWithNoDescription++;
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfObjectPropertiesWithNoDescription;
		return ((double) (numberOfObjectPropertiesWithNoDescription)) / numberOfEntities;		
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
