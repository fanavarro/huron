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

public class NumberOfLexicalRegularitiesMetric extends OntoenrichMetric {
	public NumberOfLexicalRegularitiesMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NumberOfLexicalRegularitiesMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	private static final String NAME = "Number of lexical regularities";

	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		
		
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		// Create detailed file with the lexical regularities if needed
//		if(super.isOpenDetailedOutputFile()){
//			for (LexicalRegularity lexicalRegularity: lexicalRegularities) {
//				String pattern = lexicalRegularity.getStrPattern();
//				String metricValue = "1";
//				boolean isLRClass = lexicalRegularity.getIsAClass();
//				for (Label label : lexicalRegularity.getIdLabelsWhereItAppears()) {
//					String classExhibitingLR = label.getIdLabel();
//					String labelExhibitingLR = label.getStrLabel();
//					this.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%s\t%s\t%s\t%s\n", this.getName(), pattern, isLRClass, classExhibitingLR, labelExhibitingLR, metricValue));
//				}
//			}
//		}
		double metricValue = lexicalRegularities.size();
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), Calendar.getInstance(), Collections.emptyList());

		return new MetricResult(metricValue);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "NumberOfLexicalRegularitiesMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.NUMBER_OF_LR;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}

}
