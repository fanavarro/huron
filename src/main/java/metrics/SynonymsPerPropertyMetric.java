package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

/**
 * The Class SynonymsPerPropertyMetric.
 */
public class SynonymsPerPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Synonyms per property";
	
	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tProperty\tMetric Value\n");
		int numberOfSynonyms = 0;
		int totalProperties = 0;
		
		for(OWLObjectProperty owlObjectProperty : super.getOntology().getObjectPropertiesInSignature()){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlObjectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlObjectProperty.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLDataProperty owlDataProperty : super.getOntology().getDataPropertiesInSignature()){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlDataProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlDataProperty.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().getAnnotationPropertiesInSignature()){
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlAnnotationProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlAnnotationProperty.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		return ((double) (numberOfSynonyms)) / totalProperties;
	
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#setParameters(um.ontoenrich.config.LaInputParameters)
	 */
	@Override
	public void setParameters(LaInputParameters parameters) {}

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}

}
