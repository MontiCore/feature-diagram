<!-- (c) https://github.com/MontiCore/monticore -->
[tool]:                      ../../../../../../../../fd-analysis/src/main/java/tool/FeatureModelAnalysisTool.java
[clitool]:                   ../../../../../../../../fd-analysis/src/main/java/tool/FACT.java
[FDtool]:                    ../../../../../../../../fd-analysis/src/main/java/de/monticore/featurediagram/FeatureDiagramTool.java


[flatzinc]: https://www.minizinc.org/doc-2.4.3/en/flattening.html
[choco]: https://choco-solver.org
> NOTE: <br>
This documentation is intended for  **modelers** who use the feature diagram languages.
The documentation for **language engineers** using or extending the feature diagram language is 
located **[here](fd-lang/src/main/grammars/de/monticore/FeatureDiagram.md)** and the
documentation for using 
or extending the feature configuration language is located 
**[here](fd-lang/src/main/grammars/de/monticore/FeatureConfiguration.md)**.

# Feature Diagram Languages in MontiCore

The models of the feature diagram language are called *feature models*. 
A feature model describes a software or system family in terms of 
(user-experienceable) features. Feature models are used as variability models in
the context of software product lines. 
This documentation does not provide a general holistic introduction to feature models
and their applications, as this is provided by several books (e.g., 
[[CE00]](https://dl.acm.org/doi/book/10.5555/345203), 
[[CN02]](https://dl.acm.org/doi/book/10.5555/501065)) and 
research papers (e.g., 
[[BSL+13]](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.650.9121&rep=rep1&type=pdf), 
[[KCH+90]](https://apps.dtic.mil/dtic/tr/fulltext/u2/a235785.pdf)).

Instead, the purpose of this documentation is to 
introduce the [textual syntax](#textual-syntax), 
describe the [feature analyses](#feature-analyses) that our feature model tool provides, and 
support setting up the tool in form of a short [tutorial](#tool-usage-documentation).

## Textual Syntax
Fig. 1 presents the textual and graphical syntax of an example feature model 
describing a product line of phones.
As in Java, each feature model can be located in a package to enable a hierarchical
name space of feature diagrams. Omitting the package statement 
defaults to an empty package.

<div align="center">
<img width="800" src="doc/CarSyntaxExample.png">
<br><b>Figure 1:</b> 
Example for the textual syntax of a feature model (left) and its visual 
representation (right). The bottom demonstrates the syntax of a feature
configuration by example.
</div><br>


The content of the feature model begins with the keyword `featurediagram` followed 
by the feature model's name (l. 3) and the feature model body, enclosed by curly 
braces. 
The body of a feature model must contain a statement introducing the root feature (l. 4).
Further, it can introduce subfeatures through feature groups (ll. 5-9) and 
cross-tree constraints (ll.11-12).

Feature configurations start with the keyword `featureconfig` followed by an optional
name of the feature configuration. Afterward, the feature configuration has to 
indicate, which feature model it belongs to. This begins with the keyword `for`, followed
by the (qualified) name of the feature model.
The body of the feature configuration, enclosed in curly braces, contains a comma-separated
list of selected feature names. 
Please note that this syntax for feature configurations does not distinguish between 
features that are not selected (yet) and features, which are "unselected". To this end, 
a different feature configuration language has to be employed, e.g., to model step-wise configuration
of feature models.

## Feature Analyses

Feature models and feature configurations can be analyzed to extract information
about the software product line and its products.
An overview of the different analyses is given by [[BSRC10]](https://doi.org/10.1016/j.is.2010.01.001).
The following table shows the analyses currently implemented in the feature model analysis tool:

| name | input | result |
| ------ | ------ | ------ |
| [all products](fd-analysis/src/main/java/tool/analyses/AllProducts.java) | feature model | list of feature configurations |
| [dead features](fd-analysis/src/main/java/tool/analyses/DeadFeatures.java) | feature model | list of features |
| [false optional features](fd-analysis/src/main/java/tool/analyses/FalseOptional.java) | feature model | list of features |
| [filter](fd-analysis/src/main/java/tool/analyses/Filter.java) | feature model & feature configuration | list of feature configurations |
| [find valid product](fd-analysis/src/main/java/tool/analyses/FindValidConfig.java) | feature model | feature configuration |
| [is valid](fd-analysis/src/main/java/tool/analyses/IsValid.java)| feature model & feature configuration | boolean |
| [is void](fd-analysis/src/main/java/tool/analyses/IsVoidFeatureModel.java) | feature model | boolean |
| [number of products](fd-analysis/src/main/java/tool/analyses/NumberOfProducts.java) | feature model | integer |



## Tools

The feature model language component provides three tools: The [FeatureModelAnalysisTool][tool], the [FeatureModelAnalysisCLITool][clitool], and the [FeatureDiagramTool][FDtool].

### [The FeatureModelAnalysisCLITool][clitool] 
The [FeatureModelAnalysisCLITool][clitool] coordinates the execution of one or more several analyses against a feature model
and, optionally, additional information (depends on the analysis kinds) in form of a CLI tool. It can be used as follows:
```java -jar FACT.jar <Car.fd> [-<analysis>]+```, where
* `<Car.fd>` is the (optionally, qualified) fileName of a feature model "Car"
* `<analysis>` is the name of an analysis followed by arguments for the analysis that depend on the type of analysis.

Currently, the FeatureModelAnalysisCLITool supports the following analyses:
* `isValid <Basic.fc>`, the check whether a passed configuration "Basic" is valid w.r.t the feature model.

For example, `java -jar FACT.jar Car.fd -isValid Basic.fc` checks whether a configuration "Basic" is a valid configuration of the feature model "Car". 
The result, in this case `true` or `false`, is printed to the console.


### [The FeatureModelAnalysisTool][tool] 
The [FeatureModelAnalysisTool][tool] coordinates the execution of one or more analyses against a feature model
and, optionally, additional information (depends on the analysis kinds) such as a feature configuration, in form of a Java API.
It contains the following constructors and methods:
* `FeatureModelAnalysisTool(ASTFeatureDiagram featureModel, ISolver solver)` instantiates the tool with the AST of the passed 
  featureModel and uses the passed solver for conducting the analses.
* `FeatureModelAnalysisTool(ASTFeatureDiagram featureModel)` instantiates the tool with the AST of the passed featureModel. By default, a Solver based on [Choco][choco] is employed.
* `void addAnalysis(Analysis analysis)` adds an analysis to the set of analyses conducted in this tool. Arguments for the analysis have to be added to each analysis object individually. 
* `void performAnalyses()` performs the analyses. The analysis results are then available in each Analysis object

### [The FeatureDiagramTool][FDtool] 
The [FeatureDiagramTool][FDtool] offers a Java API for processing FeatureDiagram models. 
It contains the following (static) methods:
* `ASTFDCompilationUnit parse(String modelFile)` processes the model at the passed path and produces an AST
* `FeatureDiagramArtifactScope createSymbolTable(String modelFile, ModelPath mp)` parses the model at the passed path and 
  instantiates the symbol table using passed modelpath entries for finding imported feature diagram models
* `FeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast, ModelPath mp)` instantiates the symbol table 
  using the passed AST as basis and the passed modelpath entries for finding imported feature diagram models
* `void checkCoCos(ASTFDCompilationUnit ast)` checks all context conditions of the feature diagram language against the passed AST
* `File storeSymbols(ASTFDCompilationUnit ast, String fileName)` stores the symbol table for the passed ast in a file with the path fileName. 
  If the file exists, it is overridden. Otherwise, a new file is created.
* `ASTFeatureDiagram run(String modelFile, ModelPath mp)` parses the passed modelFile, creates the symbol table, 
  checks the context conditions, and then stores the symbol table.
* `ASTFeatureDiagram run(String modelFile)` parses the passed modelFile, creates the symbol table, checks the context conditions, and stores symbol table - all
  without an explicit modelpath. Care: this can only take into account imported feature diagrams if these are located next to the passed feature diagram modelFile.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

