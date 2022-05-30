package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

public class DataPropertiesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no name";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tDataProperty\n");
		int numberOfDataPropertiesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().getDataPropertiesInSignature()){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlDataProperty);
			if (localNumberOfNames == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\n", this.getName(), owlDataProperty.toStringID()));
				numberOfDataPropertiesWithNoName++;
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfDataPropertiesWithNoName;
		return ((double) (numberOfDataPropertiesWithNoName)) / numberOfEntities;		
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
