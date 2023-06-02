package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class DataPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no description";
	
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tDataProperty\tWithNoDescription\n");
		int numberOfDataPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlDataProperty);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), true));
				numberOfDataPropertiesWithNoDescription++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), false));
			}
			numberOfEntities ++;
		}
		
		return ((double) (numberOfDataPropertiesWithNoDescription)) / numberOfEntities;		
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}

}
