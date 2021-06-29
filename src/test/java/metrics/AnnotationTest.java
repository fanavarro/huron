package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
			double result = metric.calculate();
			System.out.println(String.format("%s:\t%.3f", metric.getName(),result));
		}
	}
}
