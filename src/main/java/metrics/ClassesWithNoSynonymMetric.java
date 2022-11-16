package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import services.OntologyUtils;
import um.ontoenrich.config.LaInputParameters;

public class ClassesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no synonym";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tClass\tWithNoSynonym\n");
		int numberOfClassesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().getClassesInSignature()){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology())) {
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlClass);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), true));
			numberOfClassesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), false));
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfClassesWithNoSynonym;
		return ((double) (numberOfClassesWithNoSynonym)) / numberOfEntities;		
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
