package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class NumberOfLexicalRegularityClassesMetric extends OntoenrichMetric {
	private static final String NAME = "Number of lexical regularities classes";

	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tLexical regularity\tLexical regularity class\tIs class\tClass exhibiting the LR\tLabel of class exhibiting the LR\tMetric Value\n");
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();
		
		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold);
		
		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);
		
		// Create detailed file with the lexical regularities if needed
		if(super.isOpenDetailedOutputFile()){
			for (LexicalRegularity lexicalRegularity: lexicalRegularities) {
				String pattern = lexicalRegularity.getStrPattern();
				String metricValue = "1";
				String lrClass = lexicalRegularity.getIdLabelsWhereItAppears().parallelStream()
						.filter(x -> (x.getStrLabel().equalsIgnoreCase(lexicalRegularity.getStrPattern())))
						.findFirst().orElse(new Label("",""))
						.getIdLabel();
				boolean isLRClass = lexicalRegularity.getIsAClass();
				for (Label label : lexicalRegularity.getIdLabelsWhereItAppears()) {
					String classExhibitingLR = label.getIdLabel();
					String labelExhibitingLR = label.getStrLabel();
					this.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%s\t%s\t%s\t%s\t%s\n", this.getName(), pattern, lrClass, isLRClass, classExhibitingLR, labelExhibitingLR, metricValue));
				}
			}
		}
		return lexicalRegularities.size();
	}

	@Override
	public String getName() {
		return NAME;
	}

}
