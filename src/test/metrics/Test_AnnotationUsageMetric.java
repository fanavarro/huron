package metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * The Class Test_AnnotationUsageMetric.
 */
public class Test_AnnotationUsageMetric {

	/** The Constant ONTOLOGY. */
	private static final String ONTOLOGY = "example1.owl";
	
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
		Metric m = new AnnotationUsageMetric(ANNOTATION);
		m.setOntologyPath(getSourceOntology(ONTOLOGY));
		double res = m.calculate();
		System.out.println(String.format("Usage of %s is %d", ANNOTATION, (int)res));
	}
	
	/**
	 * Gets the source ontology.
	 *
	 * @param filename the filename
	 * @return the source ontology
	 */
	private static String getSourceOntology(String filename) {
		return (new File("src/test/metrics/" + filename)).getAbsolutePath();
	}

}
