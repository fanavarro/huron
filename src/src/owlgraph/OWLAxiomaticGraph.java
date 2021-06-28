package owlgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.github.fanavarro.graphlib.algorithms.shortest_path.PathNode;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathInput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathOutput;

/**
 * Class representing an owl-based graph, where the edges between two owl classes are owl axioms.
 * @author fabad
 *
 */
public class OWLAxiomaticGraph extends OWLGraph<OWLAxiom>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7770042274902855272L;
	
	/** The ignored axioms. */
	private Collection<AxiomType<?>> ignoredAxioms;
	
	/**
	 * Instantiates a new OWL axiomatic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 */
	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
		this.ignoredAxioms = new HashSet<>();
	}
	

	/**
	 * Instantiates a new OWL axiomatic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 * @param includeImportsClosure the include imports closure
	 */
	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
		this.ignoredAxioms = new HashSet<>();
	}

	/**
	 * Instantiates a new OWL axiomatic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 * @param ignoredAxioms the ignored axioms
	 */
	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, Collection<AxiomType<?>> ignoredAxioms) {
		super(ontology, reasonerFactory);
		this.ignoredAxioms = ignoredAxioms;
	}
	

	/**
	 * Instantiates a new OWL axiomatic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 * @param includeImportsClosure the include imports closure
	 * @param ignoredAxioms the ignored axioms
	 */
	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure, Collection<AxiomType<?>> ignoredAxioms) {
		super(ontology, reasonerFactory, includeImportsClosure);
		this.ignoredAxioms = ignoredAxioms;
	}
	
	/**
	 * Instantiates a new OWL axiomatic graph.
	 *
	 * @param reasoner the reasoner
	 * @param includeImportsClosure the include imports closure
	 * @param ignoredAxioms the ignored axioms
	 */
	public OWLAxiomaticGraph(OWLReasoner reasoner, boolean includeImportsClosure, Collection<AxiomType<?>> ignoredAxioms) {
		super(reasoner, includeImportsClosure);
		this.ignoredAxioms = ignoredAxioms;
	}
	


	/* (non-Javadoc)
	 * @see com.github.fanavarro.graphlib.Graph#getAdjacentNodesByEdgeMap(java.lang.Object)
	 */
	@Override
	public Map<OWLAxiom, Set<OWLClass>> getAdjacentNodesByEdgeMap(OWLClass node) {
		Map<OWLAxiom, Set<OWLClass>> adjacentNodesWithEdges = new HashMap<>();
		Set<OWLAxiom> axioms = node.getReferencingAxioms(getOntology());
		for(OWLAxiom axiom : axioms){
			if(ignoreAxiom(axiom, node)){
				continue;
			}
			adjacentNodesWithEdges.putIfAbsent(axiom, new HashSet<OWLClass>());
			for(OWLClass adjacentClass : axiom.getClassesInSignature()){
				if(!adjacentClass.equals(node)){
					//System.out.println(node + "\t" + axiom + "\t" + adjacentClass);
					adjacentNodesWithEdges.get(axiom).add(adjacentClass);
				}
			}
		}
		return adjacentNodesWithEdges;
	}

	/**
	 * Ignore axiom.
	 *
	 * @param axiom the axiom
	 * @param node the node
	 * @return true, if successful
	 */
	private boolean ignoreAxiom(OWLAxiom axiom, OWLClass node) {
		if (!(axiom instanceof OWLLogicalAxiom) || ignoredAxioms.contains(axiom.getAxiomType())) {
			return true;
		}
		if (axiom instanceof OWLSubClassOfAxiom){
			OWLSubClassOfAxiom aux = (OWLSubClassOfAxiom) axiom;
			return !node.equals(aux.getSubClass());
		} else if (axiom instanceof OWLEquivalentClassesAxiom){
			OWLEquivalentClassesAxiom aux = (OWLEquivalentClassesAxiom) axiom;
			List<OWLClassExpression> classExpressions = aux.getClassExpressionsAsList();
			return !node.equals(classExpressions.get(0));
		}
		return false;
	}


	/**
	 * Exists path.
	 *
	 * @param a the a
	 * @param b the b
	 * @return true, if successful
	 */
	public boolean existsPath(OWLClass a, OWLClass b){
		ShortestPathAlgorithm<OWLClass, OWLAxiom> shortestPathAlgorithm = new ShortestPathAlgorithm<OWLClass, OWLAxiom>();
		ShortestPathInput<OWLClass, OWLAxiom> input = new ShortestPathInput<OWLClass, OWLAxiom>(); 
		input.setGraph(this);
		input.setMaxDepth(-1);
		input.setSourceNode(a);
		input.setTargetNode(b);
		ShortestPathOutput<OWLClass, OWLAxiom> output = shortestPathAlgorithm.apply(input);
		List<PathNode<OWLClass, OWLAxiom>> path = output.getPath();
		return path != null && !path.isEmpty();
	}
	
	/**
	 * Checks if is axiomatically related.
	 *
	 * @param a the a
	 * @param b the b
	 * @return true, if is axiomatically related
	 */
	public boolean isAxiomaticallyRelated(OWLClass a, OWLClass b){
		return existsPath(a, b) || existsPath(b, a);
	}

}
