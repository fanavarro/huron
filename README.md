# HUman Readability ONtology (HURON)
*HURON* is a command line java application for ontology metrics calculation.

Particularly, *HURON* is able to compute the following metrics, which are classified in structural accuracy and readability metrics:

- Structural accuracy metrics:
    - *Lexically suggest logically define (LSLD)*. It computes the degree in which classes whose label is detected as a lexical regularity in the ontology are semantically connected to another classes exhibiting the lexical regularity.
    - *Systematic naming*. It computes the degree in which the subclasses of the classes whose label is a lexical regularity exhibit the lexical regularity.
- Readability metrics:
    - *Names per class*. The average names per class in the ontology.
    - *Names per object property*. The average names per object property in the ontology.
    - *Names per data property*. The average names per data property in the ontology.
    - *Names per annotation property*. The average names per annotation property in the ontology.
    - *Synonyms per class*. The average synonyms per class in the ontology.
    - *Synonyms per object property*. The average synonyms per object property in the ontology.
    - *Synonyms per data property*. The average synonyms per data property in the ontology.
    - *Synonyms per annotation property*. The average synonyms per annotation property in the ontology.
    - *Descriptions per class*. The average descriptions per class in the ontology.
    - *Descriptions per object property*. The average descriptions per object property in the ontology.
    - *Descriptions per data property*. The average descriptions per data property in the ontology.
    - *Descriptions per annotation property*. The average descriptions per annotation property in the ontology.



# Usage
## Command
`java -jar huron.jar -i <input> -o <output> -t <threads> -v`

Where

- **input** can be an owl ontology, or a folder containing owl ontologies.
- **output** is the TSV file resulting of calculating the metrics on the input ontologies.
- **t** is the number of threads to be used.
- **v** is the verbose option, which generates a TSV file per (ontology, metric) pair with extra information at entity level.

## Dependencies
If you want to test, modify or compile the application from the source code, you will need access to the **ontoenrich-core** library, used by the application to perform the analysis of lexical regularities. The source code of this library is not available yet, but it is supported by several publications:

- [https://link.springer.com/chapter/10.1007/978-3-319-17966-7_25](https://link.springer.com/chapter/10.1007/978-3-319-17966-7_25)
- [https://hal.archives-ouvertes.fr/hal-03155057/](https://hal.archives-ouvertes.fr/hal-03155057/)

You can download this library from [here](https://semantics.inf.um.es/ontology-metrics-libs/ontoenrich-core-2.0.0-SNAPSHOT.jar).