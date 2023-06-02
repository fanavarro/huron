package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class ObjectPropertiesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no synonym";
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObjectProperty\tWithNoSynonym\n");
		int numberOfObjectPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature().collect(Collectors.toList())){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlObjectProperty);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), true));
				numberOfObjectPropertiesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), false));
			}
			numberOfEntities ++;
		}
		
		return ((double) (numberOfObjectPropertiesWithNoSynonym)) / numberOfEntities;		
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}
}
