package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.issues.IssueTypes;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

/**
 * The Class SystematicNamingMetric.
 */
public class SystematicNamingMetric extends OntoenrichMetric {
	private static final String ISSUE_MSG_TEMPLATE = "Class %s ('%s') is subclass of %s ('%s') but there are no lexical regularities in common. Genus diferentia naming style is recommended, e.g. '%s %s'";



	public SystematicNamingMetric() {
		super();
	}
	
	public SystematicNamingMetric(Config config) {
		super(config);
	}

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(SystematicNamingMetric.class.getName());
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Systematic naming";
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();

		// INIT owlReasoner
		reasoner = createReasoner(getOntology());

		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold /* Minimum Coverage (in labels) */);
				

		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);

		// STEP 4: calculate the metric
		int positiveCasesCount = 0;
		int negativeCasesCount = 0;
		for (LexicalRegularity lexicalRegularity : lexicalRegularities) {
			for (OWLClass owlClassA : this.getRepresentingClasses(lexicalEnvironment, lexicalRegularity)) {
				if (OntologyUtils.isObsolete(owlClassA, getOntology(), this.getConfig().getImports())) {
					continue;
				}
				String classALabel = lexicalEnvironment.getLabelById(owlClassA.getIRI().toString()).getStrLabel();
				Set<OWLClass> subClassesOfA = reasoner.getSubClasses(owlClassA, false).entities().collect(Collectors.toSet());
				subClassesOfA.remove(getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLNothing());
				Set<OWLClass> classeslexicallyRelatedWithA = this.getClassesWithPattern(lexicalRegularity);
				classeslexicallyRelatedWithA.remove(owlClassA);
				
				Set<OWLClass> localPositiveCases = SetUtils.intersection(subClassesOfA, classeslexicallyRelatedWithA);
				int localPositiveCasesCount = localPositiveCases.size();
				
				Set<OWLClass> localNegativeCases = SetUtils.difference(subClassesOfA, classeslexicallyRelatedWithA);
				int localNegativeCasesCount = localNegativeCases.size();
				
				double localMetricResult = (double) localPositiveCasesCount / (localPositiveCasesCount + localNegativeCasesCount);
				

				positiveCasesCount += localPositiveCasesCount;
				negativeCasesCount += localNegativeCasesCount;
				List<IssueInfoDTO> issues = new ArrayList<>();
				for(OWLClass c : localNegativeCases){
					String cLabel = lexicalEnvironment.getLabelById(c.getIRI().toString()).getStrLabel();
					String issueMsg = String.format(ISSUE_MSG_TEMPLATE, c.toStringID(), cLabel, owlClassA.toStringID(), classALabel, cLabel, classALabel);
					LOGGER.log(Level.INFO, issueMsg);
					IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.SYSTEMATIC_NAMING_ISSUE, owlClassA.toStringID(), c.toStringID(), issueMsg); 
					issues.add(issue);
				}
				this.notifyExporterListeners(ontologyIRI, owlClassA.getIRI().toString(), OWL.Class.getURI(), Double.valueOf(localMetricResult), timestamp, issues);
			}
		}
		reasoner.flush();
		reasoner.dispose();
		// STEP 5: return the calculated value
		double metricValue = (double) positiveCasesCount / (positiveCasesCount + negativeCasesCount);
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		
		return new MetricResult(metricValue);

	}

	
	/**
	 * Gets the classes with pattern.
	 *
	 * @param lexicalRegularity the lexical regularity
	 * @return the classes with pattern
	 */
	private Set<OWLClass> getClassesWithPattern(LexicalRegularity lexicalRegularity) {
		Set<OWLClass> classesWithPattern = new HashSet<OWLClass>();
		for (Label l : lexicalRegularity.getIdLabelsWhereItAppears()) {
			OWLClass classWithPattern = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(l.getIdLabel()));
			if (!OntologyUtils.isObsolete(classWithPattern, getOntology(), this.getConfig().getImports())) {
				classesWithPattern.add(classWithPattern);
			}
			
		}
		return classesWithPattern;
	}

	/**
	 * Gets the representing classes.
	 *
	 * @param la the la
	 * @param p the p
	 * @return the representing classes
	 */
	private Set<OWLClass> getRepresentingClasses(LexicalEnvironment lexicalEnvironment, LexicalRegularity lexicalRegularity) {
		
		Set<Label> labels = lexicalEnvironment.getLabel(lexicalRegularity);
		Set<OWLClass> classes = new HashSet<OWLClass>();
		for (Label label : labels) {
			label.getIdLabel();
			OWLClass owlClass = lexicalEnvironment.owlApiOntology.getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(label.getIdLabel()));
			classes.add(owlClass);
		}
		return classes;
	}
	
	/**
	 * Creates the reasoner.
	 *
	 * @param ontology the ontology
	 * @return the OWL reasoner
	 */
	private OWLReasoner createReasoner(OWLOntology ontology) {
		OWLReasonerFactory factory = new ElkReasonerFactory();
		OWLReasoner reasoner =  factory.createReasoner(ontology);
		try {
			reasoner.isConsistent();
		} catch (Exception e){
			LOGGER.warning("ELK reasoner not supported, using structural reasoner.");
			factory = new StructuralReasonerFactory();
			reasoner = factory.createReasoner(ontology);
		}
		return reasoner;
	}
	
	


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "SystematicNamingMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.SYSTEMATIC_NAMING_PRINCIPLE;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_HIGHER_BEST;
	}

}
