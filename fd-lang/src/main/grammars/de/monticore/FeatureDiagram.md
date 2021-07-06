<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

<!-- List with all references used within this markdown file: -->
[Readme]:                    ../../../../../../README.md
[Grammar]:                   ../../../../../../fd-lang/src/main/grammars/de/monticore/FeatureDiagram.mc4
[fdstc]:                     ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_symboltable/FeatureDiagramScopesGenitor.java
[serialization]:             ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_symboltable/
[HasTreeShape]:              ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/HasTreeShape.java
[CTCFeatureNamesExist]:      ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/CTCFeatureNameExists.java
[NonUniqueNameInGroup]:      ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/NonUniqueNameInGroup.java
[ValidConstraintExpression]: ../../../../../../fd-lang/src/main/java/de/monticore/featurediagram/_cocos/ValidConstraintExpression.java
[AllProducts]:               ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/AllProducts.java
[CompleteToValid]:           ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/Filter.java
[DeadFeature]:               ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/DeadFeature.java
[FalseOptional]:             ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/FalseOptional.java
[IsValid]:                   ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/IsValid.java
[IsVoid]:                    ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/IsVoidFeatureModel.java
[NumberOfProducts]:          ../../../../../../fd-analysis/src/main/java/mcfdtool/analyses/NumberOfProducts.java
[generator]:                 ../../../../../../fd-analysis/src/main/java/mcfdtool/transform

[flatzinc]: https://www.minizinc.org/doc-2.4.3/en/flattening.html
[choco]: https://choco-solver.org
[KTC90]: https://apps.dtic.mil/sti/pdfs/ADA235785.pdf

<!-- The following references should point towards the markdown files, once these exist -->
[Cardinality MLC]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4
[MCBasicTypes MLC]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[CommonExpressions MLC]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/CommonExpressions.mc4
[BasicSymbols MLC]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/BasicSymbols.mc4
[FeatureConfiguration MLC]: FeatureConfiguration.md

> NOTE: <br>
This document is intended for  **language engineers** who extend, adapt or embed the FD language.
**modelers** please look **[here][Readme]**. 

# MontiCore Feature Diagram Language (FDL)

[[_TOC_]]

The following documents the MontiCore feature diagram language FDL. 
Feature diagrams (FDs) consist of features and their relationships
used in product line engineering. 

The FDL is extensible and adaptible to tailor it for various different applications.
The FDL does not presume what a feature actually is and how it is described.

FDL provides many notational and semantic extensions of the original FDs
[[KCH+90]][KTC90]. Models of the FDL have the following characteristics:
* Each FD has a root feature
* Features are organized in a tree structure described by feature groups
* The supported kinds of groups are: 
    * usual "is child of" (ANDGroup)
    * alternative features (XORGroup)
    * selection of features (ORGroup)
    * lower and upper bound for number of selected features (CardinalityGroup)
* A feature may not be member of more than one feature group to preserve a tree
* Obligation or optionality of a feature is only useful and allowed in ANDGroups. 
* An FD may import other FDs and use the root feature of the imported FD.
  Technically this leads to loading of the full imported FD and addition as a sub-diagramm.
* Cross-tree constraints are expressions over features using logic operators, 
  such as **and** `&&`, **or** `||`, and FD-specific operators 
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

For a detailed explanation of the meaning and the tools to process FDs, please have a look at 
**[the readme][Readme]**.

Examples for the syntax of feature configurations are contained in the **[description of the feature configuration languages][FeatureConfiguration MLC]**.

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
`List<String> getFeatures` to obtain all features of an FD as list of Strings.
* `ASTFeatureGroup` defines a method `List<FeatureSymbol> getSubFeatureSymbols()` 
for retrieving all FeatureSymbols of features that are (direct) children of this group. 
* `FeatureDiagramSymbol` has a convenience
method `List<FeatureSymbol> getAllFeatures()` for retrieving all features
contained in the FD.

### Symboltable
- De-/Serialization functionality for the symbol table ([`serialization`][serialization])
- [`FeatureDiagramScopesGenitor`][fdstc] handles the creation and linking of the
  symbols after the FD is parsed.
  It creates:
  - A `FeatureDiagramSymbol` 
  - A `FeatureSymbol` for each feature.
  Note that features are defined on the first time that a feature name
  occurs in an FD. There is no other place to introduce a feature
  (as a consequence: definition and use of features are not strictly separated in the language.
  We decided for this option, because features do not really have a "body").
- If FD `Mine` imports another FD `Foreign` it holds:
    - Flat namespace of feature names, i.e., a feature name is not qualified. 
      (If this needs adaptation: This is designed in the symbol resolution, but also
       in the use of feature names)
    - All FD diagram elements of `Foreign` are integrated into `Mine`.
      Especially, all imported features can be used in `Mine`.
    - The result must still form a feature tree. Especially, an FD
      cannot be incomplete and provide, e.g., a forest of feature trees.
      (This is however a context condition only that could be adapted).
    - The symbol table of an FD does not distinguish symbols
      of locally defined and imported features. (This is unusual, but more efficient as
      an imported FD needs to be loaded completely anyway, because features don't have
      a body that is worth encapsulating)
    - The stored symboltable does also store imported symbols, so transitve import is
      not needed, but diamond import (i.e. a feature is imported via two different paths)
      is currently also not possible. This, however, is negligible as under the assumption of a 
      flat namespace of features, a diamond import would contradict the fact that feature names 
      within an FD are unique.

### Symbol kinds used by FD (importable):
- An FD (as defined here) does not use any symbol kinds from other 
  languages; it defines all symbol kinds in its own language.

### Symbol kinds defined by FD (exported):
- An FD defines its own type of FeatureSymbols.
- A `FeatureSymbol` is defined as (and has no additional body):
  ```
  class FeatureSymbol {
      String name;
  }
  ```
  - For each FD there is also a FeatureDiagramSymbol defined as:
  ```
  class FeatureDiagramSymbol {
      String name;
      /List<FeatureSymbol> allFeatures;
  }
  ```
  The FD language does not reuse the `DiagramSymbol` of the 
  [BasicSymbols][BasicSymbols MLC] language component. This is due to the fact 
  that feature configurations use the name of a feature diagram model to indicate, from which model
  its selected features originate. Thus, feature configurations check whether
  a FD model with the respective features exist. For realizing feature analyses, it is not
  sufficient for FCs to check whether a model exists that contains any diagram, as the 
  analysis requires - besides checking that the referenced feature names exist - to obtain a feature 
  tree.  

### Symbols exported by FD in the stored symboltable:
- An FD exports the diagram symbol and all its feature symbols.
- Tree structure, groups, and cross-tree constraints are **not** represented in the
  symbol table, because otherwise symbol table and FD itself would contain the same
  information anyway. If you need those details, the diagram itself should be loaded.
- The artifact scope of an FD "XY.fd" is stored in "XY.fdsym".
  Structure:
  ```
  {
    "name": "XY",
    "symbols": [
      {
        "kind": "de.monticore.featurediagram._symboltable.FeatureDiagramSymbol",
        "name": "XY",
        "features": [ "A", "B", "C" ]
      }
    ]
  }
  ```

### Context Conditions

CoCo's are implemented the following classes:

| CoCo defined in class   | Error Code | Explanation |
| ---      |  ------  |---------|
| [HasTreeShape][HasTreeShape]                 | 0xFD001 | FDs must not contain more than one root feature. |
| (see above)                                  | 0xFD002 | FDs must not contain more than one root feature. |
| (see above)                                  | 0xFD003 | FDs must contain a root feature. |
| (see above)                                  | 0xFD007 | FD rules must not introduce self loops. | 
| (see above)                                  | 0xFD008 | Each feature except the root feature must have a parent feature. | 
| (see above)                                  | 0xFD010 | The parent feature does not exist.  |
| [CTCFeatureNamesExist][CTCFeatureNamesExist] | 0xFD006 | A cross-tree constraint must operate on features that are available in the current FD. |
| [NonUniqueNameInGroup][NonUniqueNameInGroup] | 0xFD009 | A Feature group must not contain a feature more than once. |
| [ValidConstraintExpression][ValidConstraintExpression] | 0xFD011 | A cross-tree constraint is only allowed to use some kinds of expressions inherited from the common expression language component. |

## [Generator][generator]

* For minimal use: This language component provides a generator that translates
an FD to a [FlatZinc][flatzinc] model, which 
handles constraint satisfaction (and optimization) problems. Several
constraint solvers support FlatZinc as input format, which allows to find valid configurations.

### Supported Feature Analyses

The following table presents an overview of supported analyses and what they do.
In this table, we use `FM` as abbreviation for type `ASTFeatureDiagram`, 
`FC` for `ASTFeatureConfiguration`, and `Feature` for `ASTFeature`.

| Analysis Class | Input | Result | Explanation |
| ---    | ---      |  ------  |--------- |
| [IsValid][IsValid]                   | FM m, FC c | Boolean | Is c a valid FC in m? |
| [CompleteToValid][CompleteToValid]   | FM m, FC c | Optional\<FC\> | Can c be completed to a valid FC of m? If yes, return one example. |
| ---    | ---      |  ------  |--------- |
| [DeadFeatures][DeadFeature]           | FM m | Set\<Feature\> | Set of features of m not used by a valid FC. |
| [FalseOptional][FalseOptional]       | FM m | Set\<Feature\> | Features that are marked optional, but are contained in all valid FCs. |
| [IsVoid][IsVoid]                     | FM m | Boolean | Is there a valid FC in m? |
| ---    | ---      |  ------  |--------- |
| [NumberOfProducts][NumberOfProducts] | FM m | int | Returns the number of valid FCs in m. |
| [AllProducts][AllProducts]           | FM m | Set\<FC\> | Returns all valid FCs in m. Warning: The result set can be very large. |

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

