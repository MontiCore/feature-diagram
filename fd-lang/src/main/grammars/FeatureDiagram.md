<!-- List with all references used within this markdown file: -->
[Readme]: ../../../../README.md
[Grammar]: FeatureDiagram.mc4
[fdstc]: ../java/featurediagram/_symboltable/FeatureDiagramSymbolTableCreator.java
[HasTreeShape]: ../java/featurediagram/_cocos/HasTreeShape.java
[CTCFeatureNamesExist]: ../java/featurediagram/_cocos/CTCFeatureNamesExist.java
[NonUniqueNameInGroup]: ../java/featurediagram/_cocos/NonUniqueNameInGroup.java

<!-- The following references should pont towards the markdown files, once these exist -->
[Cardinality MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4
[MCBasicTypes MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[FeatureConfiguration MLC]: FeatureConfiguration.md

> NOTE: <br>
This documentation is intended for  **language engineers** who use the feature diagram languages.
The documentation for **modelers** is located **[here][Readme]**. 

# MontiCore Feature Diagram Language

[[_TOC_]]

The following documents a feature diagram language engineered with MontiCore. 
The purpose of the language is to represent feature models used in product line engineering. 
The language is extensible to tailor it for various different applications.

Many notational semantic variations and extensions to the original feature diagrams
as presented in [[KCH+90]](https://apps.dtic.mil/dtic/tr/fulltext/u2/a235785.pdf)
have been developed. This language uses feature models with the following characteristics:
* Each feature diagram must have a root feature
* A feature may not be member of more than one feature group
* The supported kinds of groups are: 
    * usual "is child of" (ANDGroup)
    * alternative features (XORGroup)
    * selection of features (ORGroup)
    * lower and upper bound for number of selected features (CardinalityGroup)
* Cross tree constraints are only binary relations between two features in which either:
    * a feature excludes another feature
    * a feature requires another feature
* Obligation or optionality of a feature in any group except ANDGroup is discarded. 
All features that are members of such groups are regarded as optional features

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

* The interface nonterminal **FDElement** can be implemented to add further 
elements to the feature diagram's body.
* The interface nonterminal **FeatureGroup** can be implemented to add further 
kinds of feature groups. However, each implementation must contain a (non-empty)
iteration of 'Feature' nonterminals. Please note, that these should be added to
the *GroupKind* enumeration in the symbol table as well.
* The interface nonterminal **ConstraintExpression** can be implemented to add further
syntax for cross-tree constraints. 

### Handwritten AST & Symbol Table Classes
The AST data structure has not been customized with any handwritten extensions to the generated classes.

The symbol table data structure has been extended with several handwritten classes as described in the 
following:

* The `FeatureDiagramSymbol` has been extended with the TOP mechanism. For convenience, we added a 
method for retrieving all features contained in the feature diagram.
* FeatureSymbols contain a list of `FeatureGroup`. Feature groups have a `GroupKind` that is either
`AND`, `XOR`, or `OR`. A feature group has children in form of a list of feature symbols.
Feature groups are instantiated during symbol table creation in the (handwritten) class
`FeatureDiagramSymbolTableCreator`.

<div align="center">
<img width="800" src="../../../../doc/SymbolTableDataStructure.png">
<br>
<b>Figure 1:</b>Symbol Table Data Structure
</div>
<br>

The symbol table is instantiated by the class [FeatureDiagramSymbolTableCreator][fdstc]. Functionality to load and store 
feature diagram symbol tables is implemented as well.

### Context Conditions

| Context Condition Class | Error Code | Explanation |
| ---      |  ------  |---------|
| [HasTreeShape][HasTreeShape]                 | 0xFD0001 | Feature diagrams must not contain more than one root feature. |
| (see above)                                  | 0xFD0002 | Feature diagrams must not contain more than one root feature. |
| (see above)                                  | 0xFD0003 | Feature diagrams must contain a root feature. |
| (see above)                                  | 0xFD0007 | Feature diagram rules must not introduce self loops. | 
| (see above)                                  | 0xFD0008 | Each feature except the root feature must have a parent feature. | 
| (see above)                                  | 0xFD0010 | The parent feature does not exist.  |
| [CTCFeatureNamesExist][CTCFeatureNamesExist] | 0xFD0006 | A cross-tree constraint must operate on features that are available in the current feature model. |
| [NonUniqueNameInGroup][NonUniqueNameInGroup] | 0xFD0009 | A Feature group must not contain a feature more than once. |

## Generator

## Related Language Components
* This language component uses the language component **[de.monticore.Cardinality][Cardinality MLC]**
* This language component uses the language component **[de.monticore.types.MCBasicTypes][MCBasicTypes MLC]**
* This language component can be used in combination with the language component **[FeatureConfiguration][FeatureConfiguration MLC]**

  

  - Was sind die wichtigsten (handgeschriebenen) internen Funktionalitäten 

    (Funktionen, die auf der abstrakten Syntax Informationen berechnen oder die abstrakte Syntax modifizieren), 

    z.B. Trafos, Symboltabellenberechnungen, CoCo checks

  - Welche Erweiterungspunkte für die Syntax sind vorgesehen? 

    (z.B. in Form von Top-Mechanismus/Pattern zur Erweiterung)

  - Welche Generatorfunktionalitäten existieren?

    (z.B. PrettyPrinter)

  - Welche Erweiterungspunkte für Generatoren sind vorgesehen?

