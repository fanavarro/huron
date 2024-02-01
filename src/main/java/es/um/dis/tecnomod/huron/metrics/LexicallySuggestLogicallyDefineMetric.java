package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyGraphService;
import es.um.dis.tecnomod.huron.services.OntologyGraphServiceImpl;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class LexicallySuggestLogicallyDefineMetric.
 */
public class LexicallySuggestLogicallyDefineMetric extends OntoenrichMetric {
	
	public LexicallySuggestLogicallyDefineMetric(Config config) {
		super(config);
		ontologyGraphService = new OntologyGraphServiceImpl();
		// TODO Auto-generated constructor stub
	}

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(LexicallySuggestLogicallyDefineMetric.class.getName());
	
	/** The max depth taken into account to check if two classes are axiomatically related */
	private static final int MAX_DEPTH = 5;
	
	/** The Constant NAME. */
	private static final String NAME = "Lexically suggest logically define";

	/** The Constant IGNORED_AXIOMS. */
	private static final List<AxiomType<?>> IGNORED_AXIOMS = Arrays.asList(AxiomType.DISJOINT_CLASSES,
			AxiomType.DISJOINT_DATA_PROPERTIES, AxiomType.DISJOINT_OBJECT_PROPERTIES, AxiomType.DISJOINT_UNION);
	
	
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	/** The ontology graph service. */
	private OntologyGraphService ontologyGraphService;

	/**
	 * Instantiates a new lexically suggest logically define metric.
	 */
	public LexicallySuggestLogicallyDefineMetric() {
		ontologyGraphService = new OntologyGraphServiceImpl();
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tClass\tClass depth\tLR\tPositive Cases\tPositive cases average depth\tPositive cases average distance to LR class\tNegative Cases\tNegative cases average depth\tNegative cases average distance to LR class\tMetric Value\n" );
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		
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
				Set<OWLClass> localPositiveCases = new HashSet<OWLClass>();
				Set<OWLClass> localNegativeCases = new HashSet<OWLClass>();
				if (OntologyUtils.isObsolete(owlClassA, getOntology(), this.getConfig().getImports())) {
					continue;
				}
				for (Label l : lexicalRegularity.getIdLabelsWhereItAppears()) {
					if (l.getIdLabel().equals(owlClassA.toStringID())) {
						continue;
					}
					OWLClass owlClassCi = getOntology().getOWLOntologyManager().getOWLDataFactory()
							.getOWLClass(IRI.create(l.getIdLabel()));
					if (OntologyUtils.isObsolete(owlClassCi, getOntology(), this.getConfig().getImports())) {
						continue;
					}
					if (ontologyGraphService.isRelated(reasoner, owlClassA, owlClassCi, IGNORED_AXIOMS, this.getConfig().getImports(), MAX_DEPTH)) {
						localPositiveCases.add(owlClassCi);
					} else {
						localNegativeCases.add(owlClassCi);
						LOGGER.log(Level.INFO, String.format("%s (%s) not related with %s (%s)",
								owlClassA.toStringID(), lexicalRegularity.getStrPattern(), owlClassCi.toStringID(), l.getStrLabel()));
						//TODO: create issue here?
//						RDFUtils.createIssue(rdfModel, metricProperty, owlClassA, String.format("The entity %s ('%s') is not related with the entity %s ('%s')",
//								owlClassA.toStringID(), lexicalRegularity.getStrPattern(), owlClassCi.toStringID(), l.getStrLabel()));
					}
				}
				double localMetricResult = (double) localPositiveCases.size() / (localPositiveCases.size() + localNegativeCases.size());
				this.notifyExporterListeners(ontologyIRI, owlClassA.getIRI().toString(), OWL.Class.getURI(), Double.valueOf(localMetricResult), timestamp);
				

				if(super.isOpenDetailedOutputFile()){
					int owlClassADepth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClassA, this.getConfig().getImports());
					double averageDepthLocalPositiveCases = this.getAverageDepth(localPositiveCases);
					double averageDepthLocalNegativeCases = this.getAverageDepth(localNegativeCases);
					double averageDistanceToLRClassLocalPositiveCases = this.getAverageDistanceToDepth(localPositiveCases, owlClassADepth);
					double averageDistanceToLRClassLocalNegativeCases = this.getAverageDistanceToDepth(localNegativeCases, owlClassADepth);
					super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\t%s\t%d\t%.3f\t%.3f\t%d\t%.3f\t%.3f\t%.3f\n", this.getName(), owlClassA.toStringID(), owlClassADepth, lexicalRegularity.getStrPattern(), localPositiveCases.size(), averageDepthLocalPositiveCases, averageDistanceToLRClassLocalPositiveCases, localNegativeCases.size(), averageDepthLocalNegativeCases, averageDistanceToLRClassLocalNegativeCases, localMetricResult));
				}
				positiveCasesCount += localPositiveCases.size();
				negativeCasesCount += localNegativeCases.size();
			}
			
		}
		reasoner.flush();
		reasoner.dispose();

		// STEP 5: return the calculated value
		double metricValue = (double) positiveCasesCount / (positiveCasesCount + negativeCasesCount);
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);

		return new MetricResult(metricValue, rdfModel);

	}

	

	/**
	 * Gets the average distance to depth.
	 *
	 * @param owlClasses the owl classes
	 * @param parentDepth the parent depth
	 * @return the average distance to depth
	 */
	private double getAverageDistanceToDepth(Set<OWLClass> owlClasses, int parentDepth) {
		int distanceToParentSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass, this.getConfig().getImports());
			if(depth != -1){
				distanceToParentSum += Math.abs(depth - parentDepth);
				totalClasses++;
			}
		}
		return (double)distanceToParentSum/totalClasses;
	}

	/**
	 * Gets the average depth.
	 *
	 * @param owlClasses the owl classes
	 * @return the average depth
	 */
	private double getAverageDepth(Set<OWLClass> owlClasses) {
		int depthSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass, this.getConfig().getImports());
			if(depth != -1){
				depthSum += depth;
				totalClasses++;
			}
		}
		return (double)depthSum/totalClasses;
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
		return NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "LexicallySuggestLogicallyDefine";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.LSLD_PRINCIPLE;
	}

}
