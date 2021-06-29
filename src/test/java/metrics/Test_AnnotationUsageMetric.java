package metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The Class Test_AnnotationUsageMetric.
 */
public class Test_AnnotationUsageMetric {

	
	/** The Constant ANNOTATION. */
	private static final String ANNOTATION = "http://www.w3.org/2000/01/rdf-schema#label";
	
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
		Metric m1 = new AnnotationUsageMetric(ANNOTATION);
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		m1.setOntology(ontology);
		double res = m1.calculate();
		System.out.println(String.format("Usage of %s is %d", ANNOTATION, (int)res));
	}
}
