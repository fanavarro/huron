# ontology-metrics
Command line java application for ontology metrics calculation.

Particularly, this application is able to compute the following metrics, which are classified in structural accuracy and readability metrics:

- Readability metrics:
    - *Classes with no name*. The ratio of number of classes with no names to all classes in the ontology
    - *Classes with no synonym*. The ratio of number of classes with no synonym to all classes in the ontology
    - *Classes with no description*. The ratio of number of classes with no description to all classes in the ontology
    - *Object properties with no name*. The ratio of number of object properties with no name to all object properties in the ontology
    - *Object properties with no synonym*. The ratio of number of object properties with no synonym to all object properties in the ontology
    - *Object properties with no description*. The ratio of number of object properties with no description to all object properties in the ontology
    - *Data properties with no name*. The ratio of number of data properties with no name to all data properties in the ontology
    - *Data properties with no synonym*. The ratio of number of data properties with no synonym to all data properties in the ontology
    - *Data properties with no description*. The ratio of number of data properties with no description to all data properties in the ontology
    - *Annotation properties with no name*. The ratio of number of annotation properties with no name to all annotation properties in the ontology
    - *Annotation properties with no synonym*. The ratio of number of annotation properties with no synonym to all annotation properties in the ontology
    - *Annotation properties with no description*. The ratio of number of annotation properties with no description to all annotation properties in the ontology


# Usage
## Command
`java -jar metrics.jar -i <input> -o <output> -t <threads> -v`

Where

- **input** can be an owl ontology, or a folder containing owl ontologies.
- **output** is the TSV file resulting of calculating the metrics on the input ontologies.
- **t** is the number of threads to be used. This option is currently ignored due to blocking threads problems in the external libraries that we used.
- **v** is the verbose option, which generates a TSV file per (ontology, metric) pair with extra information at entity level.

## Dependencies
If you want to test, modify or compile the application from the source code, you will need to download the following libraries that are used by the application:

- **commons-cli-1.4.jar**, used for parsing the command line arguments. [Link](https://commons.apache.org/proper/commons-cli/).
- **graphlib-0.0.2.jar**, used for computing the existing paths between ontology classes. [Link](https://github.com/fanavarro/graphlib).
- **ontoenrich-core-2.0.0.jar**, used for extracting the lexical regularities of the ontologies. The source code of this library is not available yet, but it is supported by several publications:
    -  [https://link.springer.com/chapter/10.1007/978-3-319-17966-7_25](https://link.springer.com/chapter/10.1007/978-3-319-17966-7_25)
    - [https://hal.archives-ouvertes.fr/hal-03155057/](https://hal.archives-ouvertes.fr/hal-03155057/)

You can download these libraries from [here](http://semantics.inf.um.es/ontology-metrics-libs/libs.zip).