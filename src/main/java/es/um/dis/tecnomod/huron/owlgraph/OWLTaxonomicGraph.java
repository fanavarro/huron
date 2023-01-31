package es.um.dis.tecnomod.huron.owlgraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.github.fanavarro.graphlib.algorithms.Algorithm;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeAlgorithm;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeInput;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeOutput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathInput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathOutput;

import es.um.dis.tecnomod.huron.main.Prefixes;

/**
 * Class representing an owl-based graph, where the nodes (owl classes) are linked by sub class of axioms.
 * @author fabad
 *
 */
public class OWLTaxonomicGraph extends OWLGraph<IRI> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 807975126080016689L;
	
	/** The sub class of iri. */
	IRI SUB_CLASS_OF_IRI = IRI.create(Prefixes.RDFS + "subClassOf");

	

	/**
	 * Instantiates a new OWL taxonomic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 * @param includeImportsClosure the include imports closure
	 */
	public OWLTaxonomicGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
	}

	/**
	 * Instantiates a new OWL taxonomic graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 */
	public OWLTaxonomicGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
	}

	/**
	 * Instantiates a new OWL taxonomic graph.
	 *
	 * @param reasoner the reasoner
	 * @param includeImportsClosure the include imports closure
	 */
	public OWLTaxonomicGraph(OWLReasoner reasoner, boolean includeImportsClosure) {
		super(reasoner, includeImportsClosure);
	}

	/**
	 * Instantiates a new OWL taxonomic graph.
	 *
	 * @param reasoner the reasoner
	 */
	public OWLTaxonomicGraph(OWLReasoner reasoner) {
		super(reasoner);
	}

	/* (non-Javadoc)
	 * @see com.github.fanavarro.graphlib.Graph#getAdjacentNodesByEdgeMap(java.lang.Object)
	 */
	@Override
	public Map<IRI, Set<OWLClass>> getAdjacentNodesByEdgeMap(OWLClass node) {
		Map <IRI, Set<OWLClass>> adjacentClasses = new HashMap<IRI, Set<OWLClass>>();
		adjacentClasses.put(SUB_CLASS_OF_IRI, getReasoner().getSuperClasses(node, true).entities().collect(Collectors.toSet()));
		return adjacentClasses;
	}
	
	/**
	 * Gets the taxonomic similarity.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the taxonomic similarity
	 */
	public double getTaxonomicSimilarity(OWLClass a, OWLClass b){
		OWLClass root = getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		OWLClass leastCommonAncestor = this.getLeastCommonAncestor(a, b, root);
	
		int distRootLeastCommonAncestor = getMinPathDistance(leastCommonAncestor, root);
		int distALeastCommonAncestor = getMinPathDistance(a, leastCommonAncestor);
		int distBLeastCommonAncestor = getMinPathDistance(b, leastCommonAncestor);
		return (double)distRootLeastCommonAncestor/(distRootLeastCommonAncestor + distALeastCommonAncestor + distBLeastCommonAncestor);
	}

	/**
	 * Gets the least common ancestor.
	 *
	 * @param a the a
	 * @param b the b
	 * @param root the root
	 * @return the least common ancestor
	 */
	public OWLClass getLeastCommonAncestor(OWLClass a, OWLClass b, OWLClass root) {
		OWLClass leastCommonAncestor = null;
		LeastCommonNodeInput<OWLClass, IRI> leastCommonNodeInput = new LeastCommonNodeInput<OWLClass, IRI>();
		leastCommonNodeInput.setGraph(this);
		leastCommonNodeInput.setNodes(new HashSet<OWLClass>(Arrays.asList(a,b)));
		
		Algorithm<OWLClass, IRI> leastCommonNodeAlgorithm = new LeastCommonNodeAlgorithm<OWLClass, IRI>();
		LeastCommonNodeOutput<OWLClass, IRI> leastCommonNodeOutput = (LeastCommonNodeOutput<OWLClass, IRI>) this.applyAlgorithm(leastCommonNodeAlgorithm, leastCommonNodeInput);
		if(leastCommonNodeOutput.getLeastCommonNodes() != null && !leastCommonNodeOutput.getLeastCommonNodes().isEmpty()){
			leastCommonAncestor = leastCommonNodeOutput.getLeastCommonNodes().iterator().next();
		}
		leastCommonAncestor = leastCommonAncestor == null ? root:leastCommonAncestor;
		return leastCommonAncestor;
	}
	
	/**
	 * Gets the min path distance.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the min path distance
	 */
	public int getMinPathDistance(OWLClass a, OWLClass b){
		int dist1 = getPathDistance(a, b);
		int dist2 = getPathDistance(b, a);
		if(dist1 == -1){
			return dist2;
		}
		if(dist2 == -1){
			return dist1;
		}
		
		return Math.min(dist1, dist2);
	}
	
	/**
	 * Gets the path distance.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the path distance
	 */
	public int getPathDistance(OWLClass a, OWLClass b){
		int distance = -1;
		Algorithm<OWLClass, IRI> shortestPathAlgorithm = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(this);
		input.setSourceNode(a);
		input.setTargetNode(b);
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) this.applyAlgorithm(shortestPathAlgorithm, input);
		if(output.getPath() != null){
			distance = output.getPath().size();
		}
		return distance;
	}
	
	/**
	 * Gets the depth.
	 *
	 * @param owlClass the owl class
	 * @return the depth
	 */
	public int getDepth(OWLClass owlClass){
		OWLClass owlThing = getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		return getPathDistance(owlClass, owlThing);
	}


}
