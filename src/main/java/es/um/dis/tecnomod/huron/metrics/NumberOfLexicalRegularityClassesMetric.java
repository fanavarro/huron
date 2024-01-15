package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class NumberOfLexicalRegularityClassesMetric extends OntoenrichMetric {
	public NumberOfLexicalRegularityClassesMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NumberOfLexicalRegularityClassesMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	private static final String NAME = "Number of lexical regularities classes";

	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tLexical regularity\tLexical regularity class\tIs class\tClass exhibiting the LR\tLabel of class exhibiting the LR\tMetric Value\n");
		Calendar timestamp = Calendar.getInstance();
		
		Model rdfModel = ModelFactory.createDefaultModel();
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);
		
		// Create detailed file with the lexical regularities if needed
		
			for (LexicalRegularity lexicalRegularity: lexicalRegularities) {
				String pattern = lexicalRegularity.getStrPattern();
				String metricValue = "1";
				String lrClass = lexicalRegularity.getIdLabelsWhereItAppears().parallelStream()
						.filter(x -> (x.getStrLabel().equalsIgnoreCase(lexicalRegularity.getStrPattern())))
						.findFirst().orElse(new Label("",""))
						.getIdLabel();
				RDFUtils.createObservation(rdfModel, ontologyIRI, lrClass, OWL.Class.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);
				boolean isLRClass = lexicalRegularity.getIsAClass();
				if(super.isOpenDetailedOutputFile()){
					for (Label label : lexicalRegularity.getIdLabelsWhereItAppears()) {
						String classExhibitingLR = label.getIdLabel();
						String labelExhibitingLR = label.getStrLabel();
						this.writeToDetailedOutputFile(
								String.format(Locale.ROOT, "%s\t%s\t%s\t%s\t%s\t%s\t%s\n", this.getName(), pattern,
										lrClass, isLRClass, classExhibitingLR, labelExhibitingLR, metricValue));
					}
			}
		}
		
		double metricValue = lexicalRegularities.size();
		RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);

		return new MetricResult(metricValue, rdfModel);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NumberOfLexicalRegularityClasses";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NUMBER_OF_LR_CLASSES;
	}

}
