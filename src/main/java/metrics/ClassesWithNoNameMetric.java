package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import services.OntologyUtils;
import um.ontoenrich.config.LaInputParameters;

public class ClassesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {
	
	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no name";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	
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
