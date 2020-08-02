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

FDL provides many notational and semantic extensions of the original FDs
[[KCH+90]][KTC90]. FDL has the following characteristics:
* Each FD has a root feature
* Features are organized in a tree structure described by feature groups
* The supported kinds of groups are: 
    * usual "is child of" (ANDGroup)
    * alternative features (XORGroup)
    * selection of features (ORGroup)
    * lower and upper bound for number of selected features (CardinalityGroup)
* A feature may not be member of more than one feature group to preserve a tree
* Obligation or optionality of a feature is only usefula and allowed in ANDGroups. 
* A FD may import other FDs and use the root feature of the
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

The syntax of the FDL is specified through the FD 
[grammar](#grammar), some handwritten extensions of the 
[abstract syntax](#handwritten-ast-&-symbol-table-classes), and the 
[context conditions](#context-conditions).

### Grammar
The **[FeatureDiagram grammar][Grammar]** describes the syntax
of FDL and is itself a documentation of the
concrete and abstract syntax.
FDL is based on:
* **[de.monticore.Cardinality][Cardinality MLC]**
* **[de.monticore.types.MCBasicTypes][MCBasicTypes MLC]**
* **[de.monticore.espressions.CommonExpressions][CommonExpressions MLC]**

The grammar contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.
The extension points are:

* The interface nonterminal **FDElement** can be implemented to add further 
elements to the FD's body.
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
`List<String> getFeatures` to obtain all features of a FD as list of Strings.
* `ASTFeatureGroup` defines a method `List<FeatureSymbol> getSubFeatureSymbols()` 
for retrieving all FeatureSymbols of features that are (direct) children of this group. 
* `FeatureDiagramSymbol` has a convenience
method `List<FeatureSymbol> getAllFeatures()` for retrieving all features
contained in the FD.

### Symboltable
- De-/Serialization functionality for the symbol table ([`serialization`][serialization])
- [`FeatureDiagramSymbolTableCreator`][fdstc] handles the creation and linking of the
  symbols after the FD is parsed.
  It creates:
  - A `FeatureDiagramSymbol` 
  - A `FeatureSymbol` for each feature.
  Note that features are defined on the first time that a feature name
  occurs in a FD. There is no other place to introduce a feature
  (as a consequence: definition and use iof features are not strictly separated in the language.
  We decided to take this option, because features have not really a "body")
- If FD `Mine` imports another FD `Foreign` it holds:
    - Flat namespace of feature names, i.e., a feature name are not qualified. 
      (If this needs adaptation: This is designed in the symbol resolution, but also
       in the use of feature names)
    - All FD diagram elements of `Foreign` are integrated into `Mine`.
      Especially, all imported features can be used in `Mine`.
    - The result must still form a feature tree. Especially, a FD
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
- A FD (as defined here) does not import any symbols from other 
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

### Symbols exported by FD in the stored symboltable:
- A FD exports the diagram symbol and all its feature symbols.
- Tree structure, groups, and cross-tree constraints are **not** represented in the
  symbol table, because otherwise symbol table and FD itself would contain the same
  information anyway. If you need those details, the diagram itself should be loaded.
- The artifact scope of a FD "XY.fd" is stored in "XY.fdsym".
  Structure:
  ```
  TODO: show the fdsym-table of a small FD (2 FD symbols only)
  ```

### Context Conditions

CoCo's are implemented the following classes:

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

## [Generator][generator]

* For minimal use: This language component provides a generator that translates
a FD to a
[FlatZinc][flatzinc] model, which 
handles constraint satisfaction (and optimization) problems. Several
constraint solvers support FlatZinc as input format, which allows to find valid configurations.

### Supported Feature Analyses

The following table presents an overview of supported analyses and what they do.
In this table, we use `FM` as abbreviation for type `ASTFeatureDiagram`, 
`FC` for `ASTFeatureConfiguration`, and `Feature` for `ASTFeature`.

| Analysis Class | Input | Result | Explanation |
| ---    | ---      |  ------  |---------|
| [IsValid][IsValid]                   | FM m, FC c | Boolean |
			Is c a valid FC in m? |
| [CompleteToValid][CompleteToValid]   | FM m, FC c | Optional\<FC\> |
			Can c be completed to a valid FC of m? If yes, return one example. |
| ---    | ---      |  ------  |---------|
| [DeadFeatures][DeadFeature]           | FM m | Set\<Feature\> |
			Set of features of m not used by a valid FC. |
| [FalseOptional][FalseOptional]       | FM m | Set\<Feature\> |
			Features that are marked optional, but are contained in all valid FCs. |
| [IsVoid][IsVoid]                     | FM m | Boolean |
			Is there a valid FC in m? |
| ---    | ---      |  ------  |---------|
| [NumberOfProducts][NumberOfProducts] | FM m | int |
			Returns the number of valid FCs in m. |
| [AllProducts][AllProducts]           | FM m | Set\<FC\> |
			Returns all valid FCs in m.
			Warning: The result set can be very large. |

## Related Information

* Description of the language and tools  TODO --> Readme

* **[Feature configuration language (FCL)][FeatureConfiguration MLC]**
  
