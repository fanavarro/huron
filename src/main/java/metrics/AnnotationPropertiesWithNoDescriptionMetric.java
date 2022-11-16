package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

public class AnnotationPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric implements DetailedOutputHeaderMetricInterface {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "AnnotationProperties with no description";
	private int totalEntities = 0;
	private int numberOfEntitiesWithNoAnnotation = 0;
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// TODO Auto-generated method stub
		super.writeToDetailedOutputFile("Metric\tAnnotationProperty\tWithNoDescription\n");
		int numberOfAnnotationPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().getAnnotationPropertiesInSignature()){		
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlAnnotationProperty);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), true));
				numberOfAnnotationPropertiesWithNoDescription++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), false));
			}
			numberOfEntities ++;
		}
		
		totalEntities = numberOfEntities;
		numberOfEntitiesWithNoAnnotation = numberOfAnnotationPropertiesWithNoDescription;
		return ((double) (numberOfAnnotationPropertiesWithNoDescription)) / numberOfEntities;		
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
