package metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
		m1.setOntologyPath(getSourceOntology("example1.owl"));
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
		m1.setOntologyPath(getSourceOntology("example2.owl"));
		m1.setParameters(getXmlParametersLexicalAnalysis());
		//m1.openDetailedOutputFile(new File("outputFileSystematic.tsv"));

		// STEP 2: calculate the metric
		double res = m1.calculate();
		assertEquals(3.0/4.0, res, 0.01);
		System.out.println("Systematic Naming metric: " + res);
		//m1.closeDetailedOutputFile();
	}

	/**
	 * Test pato.
	 *
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void testPato() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		// STEP 1: create the object
		LexicallySuggestLogicallyDefineMetric m1 = new LexicallySuggestLogicallyDefineMetric();
		m1.setOntologyPath(getSourceOntology("pato.owl"));
		m1.setParameters(getXmlParametersLexicalAnalysis());
		

		// STEP 2: calculate the metric
		System.out.println("Systematic Naming metric: " + m1.calculate());
	}
	
	/**
	 * Test borrar 1.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void testBorrar1() throws FileNotFoundException, IOException, Exception {
		// STEP 1: create the object
		LexicallySuggestLogicallyDefineMetric m1 = new LexicallySuggestLogicallyDefineMetric();
		//m1.setOntologyPath("/home/fabad/Escritorio/SnomedCT_InternationalRF2_PRODUCTION_20190731T120000Z.owl");
		m1.setOntologyPath(getSourceOntology("example1.owl"));
		m1.setParameters(getXmlParametersLexicalAnalysis());
		m1.openDetailedOutputFile(new File("lsld.tsv"));
		m1.calculate();
		m1.closeDetailedOutputFile();
	}
	
	/**
	 * Test borrar 2.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void testBorrar2() throws FileNotFoundException, IOException, Exception {
		// STEP 1: create the object
		SystematicNamingMetric m1 = new SystematicNamingMetric();
		//m1.setOntologyPath("/home/fabad/Escritorio/SnomedCT_InternationalRF2_PRODUCTION_20190731T120000Z.owl");
		m1.setOntologyPath(getSourceOntology("example1.owl"));
		m1.setParameters(getXmlParametersLexicalAnalysis());
		m1.openDetailedOutputFile(new File("systematic_naming.tsv"));
		m1.calculate();
		m1.closeDetailedOutputFile();
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
