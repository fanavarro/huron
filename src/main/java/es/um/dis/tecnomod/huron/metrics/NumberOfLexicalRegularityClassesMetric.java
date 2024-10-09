package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

public class NumberOfLexicalRegularityClassesMetric extends OntoenrichMetric {
	public NumberOfLexicalRegularityClassesMetric() {
		super();
	}

	public NumberOfLexicalRegularityClassesMetric(Config config) {
		super(config);
	}

	private static final String NAME = "Number of lexical regularities classes";

	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		
		Calendar timestamp = Calendar.getInstance();
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);
		
		// Create detailed file with the lexical regularities if needed
		
		for (LexicalRegularity lexicalRegularity: lexicalRegularities) {
			String lrClass = lexicalRegularity.getIdLabelsWhereItAppears().parallelStream()
					.filter(x -> (x.getStrLabel().equalsIgnoreCase(lexicalRegularity.getStrPattern())))
					.findFirst().orElse(new Label("",""))
					.getIdLabel();
			this.notifyExporterListeners(ontologyIRI, lrClass, OWL.Class.getURI(), Boolean.valueOf(true), timestamp, Collections.emptyList());
		}
		
		double metricValue = lexicalRegularities.size();
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());

		return new MetricResult(metricValue);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NumberOfLexicalRegularityClassesMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.NUMBER_OF_LR_CLASSES;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}

}
