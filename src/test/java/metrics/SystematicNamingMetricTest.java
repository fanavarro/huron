package metrics;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The Class SystematicNamingMetricTest.
 */
public class SystematicNamingMetricTest {

	/**
	 * Test example 1.
	 *
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void testExample1() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {

		// STEP 1: create the object
		Metric m1 = new SystematicNamingMetric();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		m1.setOntology(ontology);

		// STEP 2: calculate the metric
		System.out.println("Systematic Naming metric: " + m1.calculate());

	}
	
	/**
	 * Test example 2.
	 *
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void testExample2() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {

		// CREATE TEMP FILE
		Path tempFile = Files.createTempFile("outputFileComplementary", ".tsv");
		// STEP 1: create the object
		Metric m1 = new SystematicNamingMetric();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example2.owl"));
		m1.setOntology(ontology);
		m1.openDetailedOutputFile(tempFile.toFile());

		// STEP 2: calculate the metric
		double res = m1.calculate();
		m1.closeDetailedOutputFile();
		assertEquals(3.0/5.0, res, 0.01);

	}
}
