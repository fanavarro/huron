package metrics;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The Class NamesPerPropertyMetricTest.
 */
public class NamesPerPropertyMetricTest {

	/**
	 * Test calculate.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	public void testCalculate() throws FileNotFoundException, IOException, Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("example1.owl"));
		Metric metric = new NamesPerPropertyMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(7.0/6.0, res, 0.01);
	}

}
