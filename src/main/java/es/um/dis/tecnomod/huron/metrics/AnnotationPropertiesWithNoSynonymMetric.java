package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


public class AnnotationPropertiesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "AnnotationProperties with no synonym";
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tAnnotationProperty\tWithNoSynonym\n");
		int numberOfAnnotationPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature().collect(Collectors.toList())){	
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlAnnotationProperty);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), true));
				numberOfAnnotationPropertiesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), false));
			}
			numberOfEntities ++;
		}
		
		return ((double) (numberOfAnnotationPropertiesWithNoSynonym)) / numberOfEntities;		
	}



	@Override
	public String getName() {
		return METRIC_NAME;
	}
}
