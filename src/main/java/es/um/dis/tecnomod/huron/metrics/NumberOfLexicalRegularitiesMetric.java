package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;

public class NumberOfLexicalRegularitiesMetric extends OntoenrichMetric {
	private static final String NAME = "Number of lexical regularities";

	@Override
	public MetricResult calculateAll() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tLexical regularity\tIs class\tClass exhibiting the LR\tLabel of class exhibiting the LR\tMetric Value\n");
		
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		// Create detailed file with the lexical regularities if needed
		if(super.isOpenDetailedOutputFile()){
			for (LexicalRegularity lexicalRegularity: lexicalRegularities) {
				String pattern = lexicalRegularity.getStrPattern();
				String metricValue = "1";
				boolean isLRClass = lexicalRegularity.getIsAClass();
				for (Label label : lexicalRegularity.getIdLabelsWhereItAppears()) {
					String classExhibitingLR = label.getIdLabel();
					String labelExhibitingLR = label.getStrLabel();
					this.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%s\t%s\t%s\t%s\n", this.getName(), pattern, isLRClass, classExhibitingLR, labelExhibitingLR, metricValue));
				}
			}
		}
		double metricValue = lexicalRegularities.size();
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
		});
		return new MetricResult(metricValue, rdfModel);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NumberOfLexicalRegularities";
	}

}
