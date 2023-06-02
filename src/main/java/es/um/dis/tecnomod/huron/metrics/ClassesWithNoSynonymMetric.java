package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.services.OntologyUtils;

public class ClassesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no synonym";
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tWithNoSynonym\n");
		int numberOfClassesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature().collect(Collectors.toList())){
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
		
		return ((double) (numberOfClassesWithNoSynonym)) / numberOfEntities;		
	}
	

	@Override
	public String getName() {
		return METRIC_NAME;
	}
}
