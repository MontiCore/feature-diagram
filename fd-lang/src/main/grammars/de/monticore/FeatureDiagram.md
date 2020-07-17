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
[clitool]:                   ../../../../../../../../fd-analysis/src/main/java/tool/FeatureModelAnalysisCLITool.java
[FDtool]:                    ../../../../../../../../fd-analysis/src/main/java/de/monticore/featurediagram/FeatureDiagramTool.java


[flatzinc]: https://www.minizinc.org/doc-2.4.3/en/flattening.html
[KTC90]: https://apps.dtic.mil/dtic/tr/fulltext/u2/a235785.pdf

<!-- The following references should point towards the markdown files, once these exist -->
[Cardinality MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4
[MCBasicTypes MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[CommonExpressions MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/CommonExpressions.mc4
[FeatureConfiguration MLC]: FeatureConfiguration.md

> NOTE: <br>
This documentation is intended for  **language engineers** who use the feature diagram language.
The documentation for **modelers** is located **[here][Readme]**. 

# MontiCore Feature Diagram Language

[[_TOC_]]

The following documents a feature diagram language engineered with MontiCore. 
The purpose of the language is to represent feature models used in product line engineering. 
The language is extensible to tailor it for various different applications.

Many notational and semantic variations of the original feature diagrams
as presented in [[KCH+90]][KTC90]
have been developed. This language uses feature models with the following characteristics:
* Each feature model must have a root feature
* The features of a feature model are in a tree structure described by feature groups
* The supported kinds of groups are: 
    * usual "is child of" (ANDGroup)
    * alternative features (XORGroup)
    * selection of features (ORGroup)
    * lower and upper bound for number of selected features (CardinalityGroup)
* A feature may not be member of more than one feature group
* Obligation or optionality of a feature in any group except ANDGroup is discarded. 
All features that are members of such groups are regarded as optional features
* A feature model may import other feature models. Through this, the root feature of an
  imported feature model can be used in the current model.   The tree induced by 
  the imported feature, as well as all cross-tree constraints, are imported as well.
* Cross-tree constraints are expressions over any features with logic operators, 
  such as **and** `&&`, **or** `||`. Cross-tree constraints can further use the operators
  `requires` and `excludes`.

## Syntax Teaser

Each feature model has a name and a body that is surrounded by curly brackets.
The body contains rules that define the feature tree. Each rule describes a 
feature group with a parent feature (left-hand side) followed by an arrow 
(`->`) and children features (right-hand side). 
The root of the feature tree is detected automatically. 
Further, a feature model may define cross-tree constraints
and use Java-like expressions to formulate these.
The example below depicts the feature model `CarNavigation` with the root
feature also named `CarNavigation`. This feature has three mandatory
subfeatures `Display`, `GPS`, and `Memory`. Further, it
has the optional subfeature `PreinstalledMaps`, indicated by the question 
mark in the and group. Besides these four subfeatures, `CarNavigation` has
two further subfeatures `VoiceControl` and `TouchControl`that are in an 
xor group, which means that each configuration must contain exactly one of 
these two features.
Groups can have arbitrary members. For instance, `Memory` has three
subfeatures `Small`, `Medium`, and `Large` that are in a common xor group.
The `Display` of the navigation must have either a `SmallScreen` behin the 
steering wheel or a `LargeScreen` (e.g., in the center of the dashobard),
or both. This is realized as an or group in the feature model. 
Further, the navigation system can have preinstalled maps. If maps are preinstalled,
at least one and at most three region maps can be selected. 
The available regions are `Europe`, `NorthAmerica`, `SouthAmerica`, `Asia`, and 
`Africa`.

The feature model further contains three cross-tree constraints. Selecting 
`TouchControl` in a configuration requires also to select `LargeScreen` for
this configuration. On the other hand, selecting `SmallScreen` in a configuration
prohibits selecting `TouchControl` in the same configuration as well. 
Apart from these constraints between two features, feature models may contain
more complex constraints that involve more than two features. In the example 
feature model below, selecting all three preinstalled maps  `Europe`, `NorthAmerica`, 
and `Asia` requires to select either a `Large` or a `Medium` memory.
 
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


## Syntax

The syntax of the feature diagram language is specified through the feature model 
[grammar](#grammar), some handwritten extensions of the 
[abstract syntax](#handwritten-ast-&-symbol-table-classes), and the 
[context conditions](#context-conditions).

### Grammar
The **[FeatureDiagram grammar][Grammar]** describes the syntax
of feature models. The grammar itself is a good documentation of the concrete and abstract syntax of 
feature models. Design decisions are documented inline. 

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
* The nonterminal **Feature** is never instantiated by the parser, but produces a
**FeatureSymbol** that is an extension point in the symbol table.
The name of a feature symbol is used throughout different places in the grammar. 
By adding an adapter that adapts a foreign symbol kind to a feature symbol, language
users can indicate that the feature name refers to a name of a certain kind defined 
in another model.

### Handwritten AST & Symbol Table Classes
The AST data structure has been customized with handwritten extensions to the 
generated classes as follows:
* The `ASTFeatureDiagram` contains a method `String getRootFeature()` for 
obtaining the root feature of the feature models as String and a method 
`List<String> getFeatures` to obtain all features of a feature diagram as list of Strings.
* The `ASTFeatureGroup` defines a method `List<FeatureSymbol> getSubFeatureSymbols()` 
for retrieving all FeatureSymbols of features that are children of this group. 

The symbol table data structure has been extended with handwritten classes as 
described in the following:
* The `FeatureDiagramSymbol` has been extended with the TOP mechanism. For convenience, we added a 
method `List<FeatureSymbol> getAllFeatures()` for retrieving all features contained in the feature diagram.

### Symboltable
- De-/Serialization functionality for the symbol table ([`serialization`][serialization])
- [`FeatureDiagramSymbolTableCreator`][fdstc] handles the creation and linking of the symbols. The symbol table creator
  creates:
  - A FeatureDiagramSymbol for each feature model
  - A FeatureSymbol on the first time that a feature name occurs in a feature model 
- All occurences of a feature name in the model refer to the the same FeatureSymbol.
- A feature model `FM` can import other feature diagrams. Importing a feature diagram `ImpFM` has the following characteristics:
    - There is a flat namespace of feature names, i.e., a feature name cannot be qualified with a feature diagram name. 
    - All feature diagram elements of `ImpFM` behave as if these were defined in `FM`. Especially, all names of imported features 
      can be used in feature diagram elements of `FM`.
    - All locally defined and imported feature tree rules of a feature model must still form a feature tree. Especially, a feature model
      cannot be incomplete and provide, e.g., a forest of feature trees.
    - The symbol table of a feature model does not distinguish feature symbols of locally defined and imported feature models. This enables
      modularization of feature models that has no effect on the symbol table infrastructure. 

### Symbol kinds used by Feature Diagrams (importable):
- A feature diagram (as defined here) does not import any symbols from other 
  languages; it defines all features locally.
- It also doesn't import classes, variables or other symbols.


### Symbol kinds defined by Feature Diagrams (exported):
- FD defines its own type of FeatureDiagramSymbols and FeatureSymbols.
- A FeatureSymbol is defined as:
  ```
  class FeatureSymbol {
      String name;
  }
  ```
  - A FeatureDiagramSymbol is defined as:
  ```
  class FeatureDiagramSymbol {
      String name;
      /List<FeatureSymbol> allFeatures;
  }
  ```

### Symbols exported by Feature Diagrams:
- A feature diagram exports the feature diagram symbol and its feature symbols
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

### [The FeatureModelAnalysisTool][tool] 
The [FeatureModelAnalysisTool][FeatureModelAnalysisTool] coordinates the execution of one or more several analyses against a feature model
and, optionally, additional information (depends on the analysis kinds) in form of a Java API.

### [The FeatureModelAnalysisCLITool][clitool] 
The FeatureModelAnalysisCLITool coordinates the execution of one or more several analyses against a feature model
and, optionally, additional information (depends on the analysis kinds) in form of a CLI tool. It can be used as follows:
`java -jar FACT.jar <fd> [analysis [analysisParam]]+`

### [The FeatureDiagramTool][FDtool] 
The [FeatureDiagramTool][FeatureDiagramTool] offers a Java API for processing FeatureDiagram models. 
It contains the following (static) methods:
* `ASTFDCompilationUnit parse(String modelFile)` processes the model at the passed path and produces an AST
* `FeatureDiagramArtifactScope createSymbolTable(String modelFile, ModelPath mp)` parses the model at the passed path and 
  instantiates the symbol table using passed modelpath entries for finding imported feature diagram models
* `FeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast, ModelPath mp)` instantiates the symbol table 
  using the passed AST as basis and the passed modelpath entries for finding imported feature diagram models
* `void checkCoCos(ASTFDCompilationUnit ast)` checks all context conditions of the feature diagram language against the passed AST
* `ASTFeatureDiagram run(String modelFile, ModelPath mp)` parses the passed modelFile, creates the symbol table, 
  and checks the context cnoditions.
* `ASTFeatureDiagram run(String modelFile)` parses the passed modelFile, creates the symbol table, and checks the context conditions 
  without an explicit modelpath. Care: this can only take into account import feature diagrams if these are located next to the passed feature diagram modelFile.

## Related Language Components
* This language component uses the language component **[de.monticore.Cardinality][Cardinality MLC]**
* This language component uses the language component **[de.monticore.types.MCBasicTypes][MCBasicTypes MLC]**
* This language component uses the language component **[de.monticore.espressions.CommonExpressions][CommonExpressions MLC]**
* This language component can be used in combination with the language component **[FeatureConfiguration][FeatureConfiguration MLC]**
* There are language components for partial configurations of feature models and for feature models with attributes

  
