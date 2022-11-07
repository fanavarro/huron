package main;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import metrics.AnnotationPropertiesWithNoDescriptionMetric;
import metrics.AnnotationPropertiesWithNoNameMetric;
import metrics.AnnotationPropertiesWithNoSynonymMetric;
import metrics.ClassesWithNoDescriptionMetric;
import metrics.ClassesWithNoNameMetric;
import metrics.ClassesWithNoSynonymMetric;
import metrics.DataPropertiesWithNoDescriptionMetric;
import metrics.DataPropertiesWithNoNameMetric;
import metrics.DataPropertiesWithNoSynonymMetric;
import metrics.DescriptionsPerAnnotationPropertyMetric;
import metrics.DescriptionsPerClassMetric;
import metrics.DescriptionsPerDataPropertyMetric;
import metrics.DescriptionsPerObjectPropertyMetric;
import metrics.DescriptionsPerPropertyMetric;
import metrics.DetailedOutputHeaderMetricInterface;
import metrics.LexicallySuggestLogicallyDefineMetric;
import metrics.Metric;
import metrics.NamesPerAnnotationPropertyMetric;
import metrics.NamesPerClassMetric;
import metrics.NamesPerDataPropertyMetric;
import metrics.NamesPerObjectPropertyMetric;
import metrics.NamesPerPropertyMetric;
import metrics.NumberOfClassesMetric;
import metrics.ObjectPropertiesWithNoDescriptionMetric;
import metrics.ObjectPropertiesWithNoNameMetric;
import metrics.ObjectPropertiesWithNoSynonymMetric;
import metrics.SynonymsPerAnnotationPropertyMetric;
import metrics.SynonymsPerClassMetric;
import metrics.SynonymsPerDataPropertyMetric;
import metrics.SynonymsPerObjectPropertyMetric;
import metrics.SynonymsPerPropertyMetric;
import metrics.SystematicNamingMetric;
import tasks.MetricCalculationDetailedTaskResult;
import tasks.MetricCalculationTask;
import tasks.MetricCalculationTaskResult;
import um.ontoenrich.config.LaInputParameters;
import um.ontoenrich.config.TypeOfDelimiterStrategy;
import um.ontoenrich.config.TypeOfTargetEntity;

/**
 * The Class Main.
 */
public class Main {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		CommandLine cmd = generateOptions(args);
		setLogLevel(cmd.getOptionValue('l', Level.INFO.getName()));
		File inputFile = new File(cmd.getOptionValue('i'));
		File outputFile = new File(cmd.getOptionValue('o'));
		File detailedOutputFile;
		int threads = Integer.parseInt(cmd.getOptionValue('t', "1"));
		boolean includeDetailedFiles = cmd.hasOption('v');
		
		if (!inputFile.exists()) {
			LOGGER.log(Level.SEVERE, String.format("'%s' not found.", args[0]));
			return;
		}
		
		if(outputFile.exists() && !outputFile.isFile()){
			LOGGER.log(Level.SEVERE, String.format("'%s' exists but it is not a file.", args[1]));
			return;
		}else {
			detailedOutputFile = new File(outputFile.getParent() + outputFile.separatorChar + "detailed_" + outputFile.getName());
		}

		List<File> ontologyFiles = new ArrayList<File>();
		if (inputFile.isFile()) {
			ontologyFiles.add(inputFile);
		} else if (inputFile.isDirectory()) {
			ontologyFiles.addAll(Arrays.asList(inputFile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					String lowerCaseName = name.toLowerCase();
					return lowerCaseName.endsWith(".owl") || lowerCaseName.endsWith(".obo") || lowerCaseName.endsWith(".rdf");
				}

			})));
		}
		List<Metric> metrics = getMetricsToCalculate(ontologyFiles);
		List<MetricCalculationTask> tasks = getMetricCalculationTasks(ontologyFiles, metrics, includeDetailedFiles);
		
		executeWithoutTaskExecutor(outputFile, detailedOutputFile,  tasks);
		//executeWithTaskExecutor(outputFile, tasks, threads);

	}
	
	/**
	 * Execute without task executor.
	 *
	 * @param outputFile the output file
	 * @param tasks the tasks
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void executeWithoutTaskExecutor(File outputFile, File detailedOutputFile, List<MetricCalculationTask> tasks) throws IOException{
		
		FileWriter fileWriter = new FileWriter(outputFile);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("File\tMetric\tValue\n");
			
		FileWriter detailedFileWriter = new FileWriter(detailedOutputFile);
		PrintWriter detailedPrintWriter = new PrintWriter(detailedFileWriter);
		detailedPrintWriter.print("File\tMetric\tValue\tNumberOfEntities\tTotalEntities\n");
		
		for (MetricCalculationTask task : tasks) {
			try {
				List<MetricCalculationTaskResult> results = task.call();

				for(MetricCalculationTaskResult result : results){
					
					printWriter.printf(Locale.ROOT, "%s\t%s\t%.3f\n", result.getOwlFile(), result.getMetricName(), result.getResult());
					printWriter.flush();
					
					if (Class.forName("tasks.MetricCalculationDetailedTaskResult").isInstance(result)) {
						MetricCalculationDetailedTaskResult r = (MetricCalculationDetailedTaskResult) result;
						detailedPrintWriter.printf(Locale.ROOT, "%s\t%s\t%.3f\t%d\t%d\n", result.getOwlFile(), result.getMetricName(), result.getResult(), r.getDividend(), r.getDivisor());
						detailedPrintWriter.flush();						
					}
				}
			} catch (Exception e) {
				String msg = String.format("Error processing %s:\n%s", task.getOntologyFile().getAbsolutePath(), e.getMessage());
				LOGGER.log(Level.SEVERE, msg, e);
			}
		}
		detailedPrintWriter.close();
		printWriter.close();
	}

	/**
	 * Execute with task executor.
	 *
	 * @param outputFile the output file
	 * @param tasks the tasks
	 * @param threads the threads
	 * @throws InterruptedException the interrupted exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void executeWithTaskExecutor(File outputFile, List<MetricCalculationTask> tasks, int threads)
			throws InterruptedException, IOException {
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		//ExecutorService executor = Executors.newSingleThreadExecutor(); // The owlapi version used in this project is not thread-safe...	
		List<Future<List<MetricCalculationTaskResult>>> futureResults = executor.invokeAll(tasks);
		FileWriter fileWriter = new FileWriter(outputFile);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("File\tMetric\tValue\n");
		for(Future<List<MetricCalculationTaskResult>> futureResult : futureResults){
			try {
				List<MetricCalculationTaskResult> results = futureResult.get();
				for(MetricCalculationTaskResult result : results){
					printWriter.printf(Locale.ROOT, "%s\t%s\t%.3f\n", result.getOwlFile(), result.getMetricName(), result.getResult());
				}
			} catch (ExecutionException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		printWriter.close();
		executor.shutdown();
	}
	
	/**
	 * Gets the metric calculation tasks.
	 *
	 * @param ontologyFiles the ontology files
	 * @param metrics the metrics
	 * @param includeDetailedFiles the include detailed files
	 * @return the metric calculation tasks
	 */
	private static List<MetricCalculationTask> getMetricCalculationTasks(List<File> ontologyFiles, List<Metric> metrics, boolean includeDetailedFiles){
		List<MetricCalculationTask> tasks = new ArrayList<MetricCalculationTask>();
		for(File ontologyFile : ontologyFiles){
			LaInputParameters parameters = getXmlParametersLexicalAnalysis(ontologyFile.getName());
			tasks.add(new MetricCalculationTask(metrics, ontologyFile, parameters, includeDetailedFiles));
			
		}
		return tasks;
	}

	/**
	 * Gets the xml parameters lexical analysis.
	 *
	 * @param description the description
	 * @return the xml parameters lexical analysis
	 */
	private static LaInputParameters getXmlParametersLexicalAnalysis(String description) {
		Boolean caseSensitive = false;
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
	
	/**
	 * Generate options.
	 *
	 * @param args the args
	 * @return the command line
	 */
	private static CommandLine generateOptions(String[] args){
		Options options = new Options();
		
		Option input = new Option("i", "input", true, "input owl file path, or folder containing owl files.");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output tsv file with the metrics");
        output.setRequired(true);
        options.addOption(output);
        
        Option threads = new Option("t", "threads", true, "number of threads");
        threads.setRequired(false);
        options.addOption(threads);
        
        Option logLevel = new Option("l", "log-level", true, "log level (SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL )");
        logLevel.setRequired(false);
        options.addOption(logLevel);
        
        Option generateDetailedFiles = new Option("v", "detailed-files", false, "Generate a report for each metric.");
        generateDetailedFiles.setRequired(false);
        options.addOption(generateDetailedFiles);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;;
        try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("metrics", options);
			System.exit(1);
		}
        
        return cmd;
	}
	
	/**
	 * Sets the log level.
	 *
	 * @param level the new log level
	 */
	private static void setLogLevel(String level){
		Logger root = Logger.getLogger("");
		root.setLevel(Level.parse(level));
	}
	
	/**
	 * Gets the metrics to calculate.
	 *
	 * @param ontologyFiles the ontology files
	 * @return the metrics to calculate
	 */
	private static List<Metric> getMetricsToCalculate(List<File> ontologyFiles){
		LOGGER.log(Level.INFO, "Obtaining metrics to calculate");
		List<Metric> metrics = new ArrayList<Metric>();
//		metrics.add(new LexicallySuggestLogicallyDefineMetric());
//		metrics.add(new SystematicNamingMetric());
//		metrics.add(new NumberOfClassesMetric());
//		metrics.add(new NamesPerClassMetric());
//		metrics.add(new NamesPerPropertyMetric());
//		metrics.add(new NamesPerAnnotationPropertyMetric());
//		metrics.add(new NamesPerDataPropertyMetric());
//		metrics.add(new NamesPerObjectPropertyMetric());
//		metrics.add(new SynonymsPerClassMetric());
//		metrics.add(new SynonymsPerPropertyMetric());
//		metrics.add(new SynonymsPerAnnotationPropertyMetric());
//		metrics.add(new SynonymsPerDataPropertyMetric());
//		metrics.add(new SynonymsPerObjectPropertyMetric());
//		metrics.add(new DescriptionsPerClassMetric());
//		metrics.add(new DescriptionsPerPropertyMetric());
//		metrics.add(new DescriptionsPerAnnotationPropertyMetric());
//		metrics.add(new DescriptionsPerDataPropertyMetric());
//		metrics.add(new DescriptionsPerObjectPropertyMetric());
		metrics.add(new ClassesWithNoNameMetric());
		metrics.add(new ClassesWithNoSynonymMetric());
		metrics.add(new ClassesWithNoDescriptionMetric());
		metrics.add(new ObjectPropertiesWithNoNameMetric());
		metrics.add(new ObjectPropertiesWithNoSynonymMetric());
		metrics.add(new ObjectPropertiesWithNoDescriptionMetric());
		metrics.add(new DataPropertiesWithNoNameMetric());
		metrics.add(new DataPropertiesWithNoSynonymMetric());
		metrics.add(new DataPropertiesWithNoDescriptionMetric());		
		metrics.add(new AnnotationPropertiesWithNoNameMetric());
		metrics.add(new AnnotationPropertiesWithNoSynonymMetric());
		metrics.add(new AnnotationPropertiesWithNoDescriptionMetric());
			
		
		LOGGER.log(Level.INFO, "Metrics obtained");
		return metrics;
	}

}
