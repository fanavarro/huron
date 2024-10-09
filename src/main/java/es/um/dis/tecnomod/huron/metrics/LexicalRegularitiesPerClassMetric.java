package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

public class LexicalRegularitiesPerClassMetric extends OntoenrichMetric {
	
	/** The Constant METRIC_NAME. */
	private static final String  METRIC_NAME = "Lexical regularities per class";
	
	public LexicalRegularitiesPerClassMetric() {
		super();
	}


	public LexicalRegularitiesPerClassMetric(Config config) {
		super(config);
	}

	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		long nclasses = this.getNumberOfClasses();

		double metricValue = Double.valueOf(lexicalRegularities.size()) / Double.valueOf(nclasses);
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), Calendar.getInstance(), Collections.emptyList());

		return new MetricResult(metricValue);
	}

	

	@Override
	public String getName() {
		return METRIC_NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "LexicalRegularitiesPerClassMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.NUMBER_OF_LRS_PER_CLASS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}

}
