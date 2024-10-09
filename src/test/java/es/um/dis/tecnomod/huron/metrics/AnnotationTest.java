package es.um.dis.tecnomod.huron.metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.services.OntologyUtils;

/**
 * The Class AnnotationTest.
 */
public class AnnotationTest {
	

	/**
	 * Test.
	 *
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void test() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		List<Metric> metrics = Arrays.asList(new NamesPerClassMetric(), new SynonymsPerClassMetric(),
				new DescriptionsPerClassMetric(), new NamesPerObjectPropertyMetric(), new SynonymsPerObjectPropertyMetric(),
				new DescriptionsPerObjectPropertyMetric(), new NamesPerDataPropertyMetric(), new SynonymsPerDataPropertyMetric(),
				new DescriptionsPerDataPropertyMetric(), new NamesPerAnnotationPropertyMetric(), new SynonymsPerAnnotationPropertyMetric(),
				new DescriptionsPerAnnotationPropertyMetric());
		
		for(Metric metric : metrics){
			metric.setOntology(ontology);
			double result = metric.calculateValue();
			System.out.println(String.format("%s:\t%.3f", metric.getName(),result));
		}
	}
	
	@Test
	public void test2() throws OWLOntologyCreationException {
		List<String> ontologyPaths = Arrays.asList("/home/fabad/test_embed_comp/ontologies/foodon-full.owl", "/home/fabad/test_embed_comp/ontologies/go.owl" , "/home/fabad/test_embed_comp/ontologies/lkif-full-labelled.owl", "/home/fabad/test_embed_comp/ontologies/snomed.owl");
		for (String ontologyPath : ontologyPaths) {
			int numberOfClassAnnotations = this.getNumberOfClassAnnotations(ontologyPath);
			System.out.println(String.format("%s -> %d class annotations", new File(ontologyPath).getName(), numberOfClassAnnotations));
		}
	}
	
	private int getNumberOfClassAnnotations(String ontologyPath) throws OWLOntologyCreationException {
		AtomicInteger numberOfClassAnnotations = new AtomicInteger(0);
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(new File(ontologyPath));
		ontology.classesInSignature().forEach(owlClass -> {
			numberOfClassAnnotations.addAndGet(getNumberOfAnnotations(owlClass, ontology));
		} );
		return numberOfClassAnnotations.get();
	}

	private int getNumberOfAnnotations(OWLEntity entity, OWLOntology ontology){
		boolean includeImportedAnnotations = false;
		Set<OWLAnnotationAssertionAxiom> annotationAssertionAxioms = OntologyUtils.getOWLAnnotationAssertionAxiom(entity, ontology, includeImportedAnnotations);
		return annotationAssertionAxioms.size();
	}
}
