package metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import um.ontoenrich.config.LaInputParameters;
import um.ontoenrich.config.TypeOfDelimiterStrategy;
import um.ontoenrich.config.TypeOfTargetEntity;

/**
 * The Class LexicallySuggestLogicallyDefineMetricTest.
 */
public class LexicallySuggestLogicallyDefineMetricTest {

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
		LexicallySuggestLogicallyDefineMetric m1 = new LexicallySuggestLogicallyDefineMetric();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		m1.setOntology(ontology);
		m1.setParameters(getXmlParametersLexicalAnalysis());

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

		// STEP 1: create the object
		LexicallySuggestLogicallyDefineMetric m1 = new LexicallySuggestLogicallyDefineMetric();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example2.owl"));
		m1.setOntology(ontology);
		m1.setParameters(getXmlParametersLexicalAnalysis());

		// STEP 2: calculate the metric
		double res = m1.calculate();
		assertEquals(3.0/4.0, res, 0.01);
		System.out.println("Systematic Naming metric: " + res);
	}



	/**
	 * Gets the xml parameters lexical analysis.
	 *
	 * @return the xml parameters lexical analysis
	 */
	private static LaInputParameters getXmlParametersLexicalAnalysis() {

		String description = "LexAnal_TestOntology";
		Boolean caseSensitive = true;
		TypeOfTargetEntity typeTargetEntitites = TypeOfTargetEntity.CLASS_RDF_LABELS;
		TypeOfDelimiterStrategy typeDelimiterStrat = TypeOfDelimiterStrategy.CHARACTER_BLANK;
		LinkedList<Double> coverages = new LinkedList<Double>();
		coverages.add(0.1);
		LinkedList<Integer> lengths = null;

		LaInputParameters laInput = new LaInputParameters();
		laInput.setDescription(description);
		laInput.setTargetEntity(typeTargetEntitites);
		laInput.setDelimiterStrategy(typeDelimiterStrat);
		laInput.setCaseSensitive(caseSensitive);
		laInput.setCoverages(coverages);
		laInput.setLengths(lengths);
		
		return laInput;

	}

}
