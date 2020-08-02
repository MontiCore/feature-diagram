<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

<!-- List with all references used within this markdown file: -->
[Readme]:                    ../../../../../../../../README.md
[Grammar]:                   ../../../../../../../../fd-lang/src/main/grammars/de/monticore/FeatureDiagram.mc4
[fdstc]:                     ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_symboltable/FeatureDiagramSymbolTableCreator.java
[serialization]:             ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_symboltable/
[HasTreeShape]:              ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/HasTreeShape.java
[CTCFeatureNamesExist]:      ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/CTCFeatureNameExists.java
[NonUniqueNameInGroup]:      ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/NonUniqueNameInGroup.java
[ValidConstraintExpression]: ../../../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/ValidConstraintExpression.java
[AllProducts]:               ../../../../../../../../fd-analysis/src/main/java/tool/analyses/AllProducts.java
[CompleteToValid]:           ../../../../../../../../fd-analysis/src/main/java/tool/analyses/Filter.java
[DeadFeature]:               ../../../../../../../../fd-analysis/src/main/java/tool/analyses/DeadFeature.java
[FalseOptional]:             ../../../../../../../../fd-analysis/src/main/java/tool/analyses/FalseOptional.java
[IsValid]:                   ../../../../../../../../fd-analysis/src/main/java/tool/analyses/IsValid.java
[IsVoid]:                    ../../../../../../../../fd-analysis/src/main/java/tool/analyses/IsVoidFeatureModel.java
[NumberOfProducts]:          ../../../../../../../../fd-analysis/src/main/java/tool/analyses/NumberOfProducts.java
[generator]:                 ../../../../../../../../fd-analysis/src/main/java/tool/transform
[tool]:                      ../../../../../../../../fd-analysis/src/main/java/tool/FeatureModelAnalysisTool.java
[clitool]:                   ../../../../../../../../fd-analysis/src/main/java/tool/FACT.java
[FDtool]:                    ../../../../../../../../fd-analysis/src/main/java/de/monticore/featurediagram/FeatureDiagramTool.java


[flatzinc]: https://www.minizinc.org/doc-2.4.3/en/flattening.html
[choco]: https://choco-solver.org
[KTC90]: https://apps.dtic.mil/dtic/tr/fulltext/u2/a235785.pdf

<!-- The following references should point towards the markdown files, once these exist -->
[Cardinality MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4
[MCBasicTypes MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[CommonExpressions MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/CommonExpressions.mc4
[FeatureConfiguration MLC]: FeatureConfiguration.md

> NOTE: <br>
This document is intended for  **language engineers** who extend, adapt or embedd the FD language.
**modelers** please look **[here][Readme]**. 

# MontiCore Feature Diagram Language (FDL)

[[_TOC_]]

The following documents the MontiCore feature diagram language FDL. 
FDs represent feature diagrams (FD) consisting of features and their relationships
used in product line engineering. 

The FDL is extensible and adaptible to tailor it for various different applications.
The FDL does not presume what a feature actually is and how it is described.

FDL provides many notational and semantic extensions of the original feature diagrams
[[KCH+90]][KTC90]. FDL has the following characteristics:
* Each feature model has a root feature
* Features are organized in a tree structure described by feature groups
* The supported kinds of groups are: 
    * usual "is child of" (ANDGroup)
    * alternative features (XORGroup)
    * selection of features (ORGroup)
    * lower and upper bound for number of selected features (CardinalityGroup)
* A feature may not be member of more than one feature group to preserve a tree
* Obligation or optionality of a feature is only usefula and allowed in ANDGroups. 
* A feature model may import other FDs and use the root feature of the
  imported FD.
  Technically this leads to a loading of the full imported FD and addition as a sub-diagramm.
* Cross-tree constraints are expressions over features using logic operators, 
  such as **and** `&&`, **or** `||` and FD-specific operators 
  `requires` and `excludes`.

## Syntax Example

```
/* (c) https://github.com/MontiCore/monticore */
featurediagram CarNavigation {

  CarNavigation -> Display & GPS & PreinstalledMaps? & Memory ; //and group

  CarNavigation -> VoiceControl ^ TouchControl; //xor group

  Memory -> Small ^ Medium ^ Large ;

  Display -> SmallScreen | LargeScreen; //or group

  PreinstalledMaps -> [1..3] of {Europe, NorthAmerica, SouthAmerica, Asia, Africa}; //cardinality group

  TouchControl requires LargeScreen ;

  SmallScreen  excludes TouchControl ;

  (Europe && NorthAmerica && Asia) requires (Large || Medium) ;

}
```

For a detailed explanation of the meaning, please have a look at 
**[the readme][Readme]**.

## Syntax

The syntax of the feature diagram language is specified through the feature model 
[grammar](#grammar), some handwritten extensions of the 
[abstract syntax](#handwritten-ast-&-symbol-table-classes), and the 
[context conditions](#context-conditions).

### Grammar
The **[FeatureDiagram grammar][Grammar]** describes the syntax
of feature models. The grammar itself is a documentation of the
concrete and abstract syntax of 
feature models. 

The grammar contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.
The extension points are:

* The interface nonterminal **FDElement** can be implemented to add further 
elements to the feature diagram's body.
* The interface nonterminal **FeatureGroup** can be implemented to add further 
kinds of feature groups. As indicated by the right-hand side of the 
interface nonterminal, each implementation must contain a (non-empty)
iteration of 'GroupPart' nonterminals (i.e., a feature name and the question 
mark indicating optionality). 
* The interface nonterminal **Expression** inherited from the language component
**CommonExpressions** can be implemented to add further syntax for cross-tree 
constraints. 
* The nonterminal **Feature** is not used for pasring, but defines a
**FeatureSymbol** that is used in the symbol table.
The name of a feature symbol is used throughout different places in the grammar. 
By adding an adapter that adapts a foreign symbol kind to a feature symbol, language
users can indicate that the feature name refers to a name of a certain kind defined 
in another model.

### Handwritten AST & Symbol Table Classes

The generated data structure has been extended to the 
generated classes using the TOP mechanism as follows:

* `ASTFeatureDiagram` contains a method `String getRootFeature()` for 
obtaining the root feature of the FD as String and a method 
`List<String> getFeatures` to obtain all features of a feature diagram as list of Strings.
* `ASTFeatureGroup` defines a method `List<FeatureSymbol> getSubFeatureSymbols()` 
for retrieving all FeatureSymbols of features that are (direct) children of this group. 
* `FeatureDiagramSymbol` has a convenience
method `List<FeatureSymbol> getAllFeatures()` for retrieving all features
contained in the feature diagram.

### Symboltable
- De-/Serialization functionality for the symbol table ([`serialization`][serialization])
- [`FeatureDiagramSymbolTableCreator`][fdstc] handles the creation and linking of the
  symbols after the FD is parsed.
  It creates:
  - A `FeatureDiagramSymbol` 
  - A `FeatureSymbol` for each feature.
  Note that features are defined on the first time that a feature name
  occurs in a feature model. There is no other place to introduce a feature
  (as a consequence: definition and use iof features are not strictly separated in the language.
  We decided to take this option, because features have not really a "body")
- If FD `Mine` imports another FD `Foreign` it holds:
    - Flat namespace of feature names, i.e., a feature name are not qualified. 
      (If this needs adaptation: This is designed in the symbol resolution, but also
       in the use of feature names)
    - All FD diagram elements of `Foreign` are integrated into `Mine`.
      Especially, all imported features can be used in `Mine`.
    - The result must still form a feature tree. Especially, a feature model
      cannot be incomplete and provide, e.g., a forest of feature trees.
      (This is however a context condition only that could be adapte).
    - The symbol table of a FD does not distinguish symbols
      of locally defined and imported features. (This is unusual, but more efficient as
      an imported FD needs to be loaded completely anyway, because features don't have
      a body that is worth encapsulating)
    - The stored symboltable does not store imported symbols, so transitive imports
      are needed in deeper hierarchies.
vs TODO-clarify
    - The stored symboltable does also store imported symbols, so transitve import is
      not needed, but diamond import (i.e. a feature is imported via two different paths)
      is currently also not possible.
vs TODO-clarify
    - The stored symboltable does also store imported symbols and their original definition 
      source, so transitve import is not needed and diamond import
      (i.e. a feature is imported via two different paths)
      is possible.

### Symbol kinds used by FD (importable):
- A feature diagram (as defined here) does not import any symbols from other 
  languages; it defines all symbols in its own language.

### Symbol kinds defined by FD (exported):
- FD defines its own type of FeatureSymbols.
- A `FeatureSymbol` is defined as (and has no additional body):
  ```
  class FeatureSymbol {
      String name;
  }
  ```
  - For each FD there is also a DiagramSymbol defined as:
  TODO: noch anpassen, dass es tatsächlich nur ein DiagramSymbol sein wird:
  ```
  class FeatureDiagramSymbol {
      String name;
      /List<FeatureSymbol> allFeatures;
  }
  ```

### Symbols exported by FD in the stored Symboltable:
- A FD exports the feature diagram symbol and its feature symbols
  for external reference.
- The tree structure, groups, and cross-tree constraints are **not** represented in the symbol table
- The artifact scope of a feature diagram "F.fd" is stored in "F.fdsym". Loading a stored symbol table 
  of a feature diagram can be used, e.g., for checking that a feature configuration refers to an existing
  feature model and that it uses only features that exist in this feature model.


### Context Conditions

| CoCo defined in class   | Error Code | Explanation |
| ---      |  ------  |---------|
| [HasTreeShape][HasTreeShape]                 | 0xFD001 | Feature diagrams must not contain more than one root feature. |
| (see above)                                  | 0xFD002 | Feature diagrams must not contain more than one root feature. |
| (see above)                                  | 0xFD003 | Feature diagrams must contain a root feature. |
| (see above)                                  | 0xFD007 | Feature diagram rules must not introduce self loops. | 
| (see above)                                  | 0xFD008 | Each feature except the root feature must have a parent feature. | 
| (see above)                                  | 0xFD010 | The parent feature does not exist.  |
| [CTCFeatureNamesExist][CTCFeatureNamesExist] | 0xFD006 | A cross-tree constraint must operate on features that are available in the current feature model. |
| [NonUniqueNameInGroup][NonUniqueNameInGroup] | 0xFD009 | A Feature group must not contain a feature more than once. |
| [ValidConstraintExpression][ValidConstraintExpression] | 0xFD011 | A cross-tree constraint is only allowed to use some kinds of expressions inherited from the common expression language component. |

## Generator

* For minimal use: This language component provides a generator that translates feature models to 
[FlatZinc][flatzinc] models. FlatZinc, as part of MiniZinc, is a modeling language
enabling to model constraint satisfaction (and optimization) problems. Several
constraint solvers support FlatZinc as input format. The generator is located [here][generator].

### Supported Feature Analyses
The following table presents an overview of supported feature diagram analysis classes
regarding their input in form of arguments and their output in form of the analysis result.
In this table, we use `FM` as abbreviated form of `ASTFeatureDiagram`, 
`FC` as abbreviated form of `ASTFeatureConfiguration`, and `Feature` as abbreviated 
form of `ASTFeature`.

| Analysis Class | Input | Result | Explanation |
| ---    | ---      |  ------  |---------|
| [AllProducts][AllProducts]           | FM m | Set\<FC\> | Returns all valid FCs in m. **Warning: The result set can be very large and the analysis can be very inefficient for larger feature models.** |
| [CompleteToValid][CompleteToValid]   | FM m, FC c | Optional\<FC\> | Can c be completed to a valid FC of m? If yes, return one example. |
| [DeadFeatures][DeadFeature]           | FM m | Set\<Feature\> | Set of features that are contained in m, but no valid FC of m uses them. |
| [FalseOptional][FalseOptional]       | FM m | Set\<Feature\> | Set of features that are optional in m, but are contained in all valid FCs of m. |
| [IsValid][IsValid]                   | FM m, FC c | Boolean | Is c a valid FC in m? |
| [IsVoid][IsVoid]                     | FM m | Boolean | Is there a valid FC in m? |
| [NumberOfProducts][NumberOfProducts] | FM m | int | Returns the number of valid FCs in m. |

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

## Related Language Components
* This language component uses the language component **[de.monticore.Cardinality][Cardinality MLC]**
* This language component uses the language component **[de.monticore.types.MCBasicTypes][MCBasicTypes MLC]**
* This language component uses the language component **[de.monticore.espressions.CommonExpressions][CommonExpressions MLC]**
* This language component can be used in combination with the language component **[FeatureConfiguration][FeatureConfiguration MLC]**
* There are language components for partial configurations of feature models and for feature models with attributes

  
