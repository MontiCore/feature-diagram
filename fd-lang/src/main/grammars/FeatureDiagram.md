<!-- List with all references used within this markdown file: -->
[Readme]: ../../../../README.md
[Grammar]: FeatureDiagram.mc4
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

The following documents a feature diagram language engineered with MontiCore. 
The purpose of the language is to represent feature models used in product line engineering. 
The language is extensible to tailor it for various different applications. 

Many notational extensions have been presented in literature such as 

[[_TOC_]]

## Syntax

The syntax of the feature diagram language is specified through the feature model 
[grammar](#grammar), some handwritten extensions of the [abstract syntax](#handwritten-ast-&-symbol-table-classes), and the [context conditions](#context-conditions).

### Grammar
The **[FeatureDiagram grammar][Grammar]** describes the syntax
of feature models. The grammar itself is a good documentation of the concrete and abstract syntax of 
feature models. Design decisions are documented inline. 

The grammar contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.

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
<br><b>Figure 1:</b> 
Symbol Table Data Structure
</div>

### Context Conditions

| Context Condition Class | Error Code | Explanation |
| ---      |  ------  |---------|
| [HasTreeShape][HasTreeShape]                 | 0xFD0001 | Feature diagrams must not contain more than one root feature. |
|                                              | 0xFD0002 | Feature diagrams must not contain more than one root feature. |
|                                              | 0xFD0003 | Feature diagrams must contain a root feature. |
|                                              | 0xFD0007 | Feature diagram rules must not introduce self loops. | 
|                                              | 0xFD0008 | Each feature except the root feature must have a parent feature. | 
|                                              | 0xFD0010 | The parent feature does not exist.  |
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

  - Verwandte Sprachen/ benutzte Sprachen (wie?, weshalb?, warum?)
