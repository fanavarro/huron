package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import services.OntologyUtils;
import um.ontoenrich.config.LaInputParameters;

/**
 * This class calculates the ratio of number of classes with no names to the number of all classes.
 * 
 * @author fjredondo
 */
public class ClassesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Classes with no name";
	
	/** Divisor of the metric ratio */
	private int totalEntities = 0;
	
	/** Dividend of the metric ratio */
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	/**
	 * Get the entities from the ontology and obtain the annotations number of each entity to calculate the metric ratio.
	 * Also, saves the totalEntities and numberOfEntitiesWithNoAnnotation.
	 * 
	 * @return The metric ratio
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tClass\n");
		int numberOfClassesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().getClassesInSignature()){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology())) {
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlClass);
			if (localNumberOfNames == 0) {
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\n", this.getName(), owlClass.toStringID()));
				numberOfClassesWithNoName++;
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfClassesWithNoName;
		return ((double) (numberOfClassesWithNoName)) / numberOfEntities;		
	}

	@Override
	public void setParameters(LaInputParameters parameters) {
		// TODO Auto-generated method stub

	}

	/**
	 * Get the name of the metric
	 * @return String
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return METRIC_NAME;
	}

	/**
	 * Get the divisor part of the metric ratio
	 * @return int
	 */
	@Override
	public int getDivisor() {
		return totalEntities;
	}

	/**
	 * Get the dividend part of the metric ratio
	 * @return int
	 */
	@Override
	public int getDividend() {
		return numberOfEntitiesWithNoAnnotation;
	}	

}
