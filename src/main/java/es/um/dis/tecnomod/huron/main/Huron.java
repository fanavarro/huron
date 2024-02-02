package es.um.dis.tecnomod.huron.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.semanticweb.owlapi.model.parameters.Imports;

import es.um.dis.tecnomod.huron.exporters.ExporterInterface;
import es.um.dis.tecnomod.huron.exporters.RDFExporter;
import es.um.dis.tecnomod.huron.exporters.WideTSVExporter;
import es.um.dis.tecnomod.huron.metrics.AnnotationPropertiesWithNoDescriptionMetric;
import es.um.dis.tecnomod.huron.metrics.AnnotationPropertiesWithNoNameMetric;
import es.um.dis.tecnomod.huron.metrics.AnnotationPropertiesWithNoSynonymMetric;
import es.um.dis.tecnomod.huron.metrics.ClassesWithNoDescriptionMetric;
import es.um.dis.tecnomod.huron.metrics.ClassesWithNoNameMetric;
import es.um.dis.tecnomod.huron.metrics.ClassesWithNoSynonymMetric;
import es.um.dis.tecnomod.huron.metrics.DataPropertiesWithNoDescriptionMetric;
import es.um.dis.tecnomod.huron.metrics.DataPropertiesWithNoNameMetric;
import es.um.dis.tecnomod.huron.metrics.DataPropertiesWithNoSynonymMetric;
import es.um.dis.tecnomod.huron.metrics.DescriptionsPerAnnotationPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.DescriptionsPerClassMetric;
import es.um.dis.tecnomod.huron.metrics.DescriptionsPerDataPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.DescriptionsPerObjectPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.DescriptionsPerPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.LexicallySuggestLogicallyDefineMetric;
import es.um.dis.tecnomod.huron.metrics.Metric;
import es.um.dis.tecnomod.huron.metrics.NamesPerAnnotationPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.NamesPerClassMetric;
import es.um.dis.tecnomod.huron.metrics.NamesPerDataPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.NamesPerObjectPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.NamesPerPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.NumberOfClassesMetric;
import es.um.dis.tecnomod.huron.metrics.NumberOfLexicalRegularitiesMetric;
import es.um.dis.tecnomod.huron.metrics.NumberOfLexicalRegularityClassesMetric;
import es.um.dis.tecnomod.huron.metrics.ObjectPropertiesWithNoDescriptionMetric;
import es.um.dis.tecnomod.huron.metrics.ObjectPropertiesWithNoNameMetric;
import es.um.dis.tecnomod.huron.metrics.ObjectPropertiesWithNoSynonymMetric;
import es.um.dis.tecnomod.huron.metrics.SynonymsPerAnnotationPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.SynonymsPerClassMetric;
import es.um.dis.tecnomod.huron.metrics.SynonymsPerDataPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.SynonymsPerObjectPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.SynonymsPerPropertyMetric;
import es.um.dis.tecnomod.huron.metrics.SystematicNamingMetric;
import es.um.dis.tecnomod.huron.tasks.MetricCalculationTask;
import es.um.dis.tecnomod.huron.tasks.MetricCalculationTaskOnlyValue;
import es.um.dis.tecnomod.huron.tasks.MetricCalculationTaskResult;

/**
 * The Class Main.
 */
public class Huron {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Huron.class.getName());
	
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
		File rdfOutput = cmd.getOptionValue("rdf", null) != null ? new File(cmd.getOptionValue("rdf")) : null;
		int threads = Integer.parseInt(cmd.getOptionValue('t', "1"));
		boolean includeDetailedFiles = cmd.hasOption('v');
		long timeout = Long.parseLong(cmd.getOptionValue('q', "-1"));
		boolean includeImports = cmd.hasOption("imports");
		
		
		
		if (!inputFile.exists()) {
			LOGGER.log(Level.SEVERE, String.format("'%s' not found.", args[0]));
			return;
		}
		
		if(outputFile.exists() && !outputFile.isFile()){
			LOGGER.log(Level.SEVERE, String.format("'%s' exists but it is not a file.", args[1]));
			return;
		}
		
		Config config = new Config();
		config.setImports(Imports.fromBoolean(includeImports));
		config.addExporter(new WideTSVExporter(outputFile));
		if (rdfOutput != null) {
			config.addExporter(new RDFExporter(rdfOutput));
		}
		
		
		List<File> ontologyFiles = new ArrayList<File>();
		if (inputFile.isFile()) {
			ontologyFiles.add(inputFile);
		} else if (inputFile.isDirectory()) {
			ontologyFiles.addAll(Arrays.asList(inputFile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					String lowerCaseName = name.toLowerCase();
					return lowerCaseName.endsWith(".owl") || lowerCaseName.endsWith(".obo") || lowerCaseName.endsWith(".rdf") || lowerCaseName.endsWith(".ttl");
				}

			})));
		}
		List<MetricCalculationTask> tasks = getMetricCalculationTasks(ontologyFiles, includeDetailedFiles, config);
		executeWithTaskExecutor(tasks, threads, timeout, config);

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
	private static void executeWithTaskExecutor(List<MetricCalculationTask> tasks, int threads, long timeout, Config config)
			throws InterruptedException, IOException {
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<List<MetricCalculationTaskResult>>> futureResults;
		if (timeout < 0) {
			futureResults = executor.invokeAll(tasks);
		} else {
			LOGGER.log(Level.INFO, String.format("Tasks will have a timeout of %d minutes", timeout));
			futureResults = executor.invokeAll(tasks, timeout, TimeUnit.MINUTES);
		}

		for(Future<List<MetricCalculationTaskResult>> futureResult : futureResults){
			try {
				//List<MetricCalculationTaskResult> results = futureResult.get();
				futureResult.get();
			} catch (ExecutionException | CancellationException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		executor.shutdown();
		
		for (ExporterInterface exporter : config.getExporters()) {
			exporter.export();
		}
	}
	
	/**
	 * Gets the metric calculation tasks.
	 *
	 * @param ontologyFiles the ontology files
	 * @param metrics the metrics
	 * @param includeDetailedFiles the include detailed files
	 * @return the metric calculation tasks
	 */
	private static List<MetricCalculationTask> getMetricCalculationTasks(List<File> ontologyFiles, boolean includeDetailedFiles, Config config){
		List<MetricCalculationTask> tasks = new ArrayList<MetricCalculationTask>();
		for(File ontologyFile : ontologyFiles){
			tasks.add(new MetricCalculationTaskOnlyValue(getMetricsToCalculate(config), ontologyFile, includeDetailedFiles));
		}
		return tasks;
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
        
        Option timeout = new Option("q", "timeout", true, "Timeout in minutes for each task");
        timeout.setRequired(false);
        options.addOption(timeout);
        
        Option importOption = new Option(null, "imports", false, "Consider imported entities from external ontologies (import clause) when calculating the metrics.");
        importOption.setRequired(false);
        options.addOption(importOption);
        
        Option rdfOutput = new Option(null, "rdf", true, "Output results in RDF in the specified file");
        rdfOutput.setRequired(false);
        options.addOption(rdfOutput);
        
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
	private static List<Metric> getMetricsToCalculate(Config config){
		LOGGER.log(Level.INFO, "Obtaining metrics to calculate");
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new NumberOfLexicalRegularitiesMetric(config));
		metrics.add(new NumberOfLexicalRegularityClassesMetric(config));
		metrics.add(new LexicallySuggestLogicallyDefineMetric(config));
		metrics.add(new SystematicNamingMetric(config));
		metrics.add(new NumberOfClassesMetric(config));
		metrics.add(new NamesPerClassMetric(config));
		metrics.add(new NamesPerPropertyMetric(config));
		metrics.add(new NamesPerAnnotationPropertyMetric(config));
		metrics.add(new NamesPerDataPropertyMetric(config));
		metrics.add(new NamesPerObjectPropertyMetric(config));
		metrics.add(new SynonymsPerClassMetric(config));
		metrics.add(new SynonymsPerPropertyMetric(config));
		metrics.add(new SynonymsPerAnnotationPropertyMetric(config));
		metrics.add(new SynonymsPerDataPropertyMetric(config));
		metrics.add(new SynonymsPerObjectPropertyMetric(config));
		metrics.add(new DescriptionsPerClassMetric(config));
		metrics.add(new DescriptionsPerPropertyMetric(config));
		metrics.add(new DescriptionsPerAnnotationPropertyMetric(config));
		metrics.add(new DescriptionsPerDataPropertyMetric(config));
		metrics.add(new DescriptionsPerObjectPropertyMetric(config));
		metrics.add(new ClassesWithNoNameMetric(config));
		metrics.add(new ClassesWithNoDescriptionMetric(config));
		metrics.add(new ClassesWithNoSynonymMetric(config));
		metrics.add(new ObjectPropertiesWithNoNameMetric(config));
		metrics.add(new ObjectPropertiesWithNoDescriptionMetric(config));
		metrics.add(new ObjectPropertiesWithNoSynonymMetric(config));
		metrics.add(new DataPropertiesWithNoNameMetric(config));
		metrics.add(new DataPropertiesWithNoDescriptionMetric(config));
		metrics.add(new DataPropertiesWithNoSynonymMetric(config));
		metrics.add(new AnnotationPropertiesWithNoNameMetric(config));
		metrics.add(new AnnotationPropertiesWithNoDescriptionMetric(config));
		metrics.add(new AnnotationPropertiesWithNoSynonymMetric(config));
		
		LOGGER.log(Level.INFO, "Metrics obtained");
		return metrics;
	}

}
