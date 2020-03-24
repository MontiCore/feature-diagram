<!-- List with all references used within this markdown file: -->
[Readme]: ../../../../README.md
[Grammar]: FeatureDiagram.mc4
[SymTabPic]: ../../../../doc/SymbolTableDataStructure.png
[SymTabPic]: ../java/featurediagram
<!-- The following references should pont towards the markdown files, once these exist -->
[Cardinality MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4
[MCBasicTypes MLC]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[FeatureConfiguration MLC]: FeatureConfiguration.md

> NOTE: <br>
This documentation is intended for  **language engineers** who use the feature diagram languages.
The documentation for **modelers** is located **[here](Readme)**. 

# MontiCore Feature Diagram Language

The following documents a feature diagram language engineered with MontiCore. 
The purpose of the language is to describe 


## Syntax

### Grammar
The **[FeatureDiagram grammar](Grammar)** describes the syntax
of feature models. The grammar itself is a good documentation of the concrete and abstract syntax of 
feature models. Design decisions are documented inline. 

### Handwritten AST & Symbol Table Classes
The AST data structure has not been customized with any handwritten extensions to the generated classes.

The symbol table data structure has been extended with several handwritten classes as described in the 
following:

* The `FeatureDiagramSymbol` has been extended with the TOP mechanism. For convenience, we added a 
method for retrieving all features contained in the feature diagram.

<div align="center">
![Symbol Table Data Structure](../../../../doc/SymbolTableDataStructure.png)

Symbol Table Data Structure
</div>
 
### Syntax Extension Points
The grammar contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.

### Context Conditions

## Generator

## Related Language Components
* This language component uses the language component **[de.monticore.Cardinality](Cardinality MLC)**
* This language component uses the language component **[de.monticore.types.MCBasicTypes](MCBasicTypes MLC)**
* This language component can be used in combination with the language component **[FeatureConfiguration](FeatureConfiguration MLC)**



- Pro Sprache soll eine eigene *.md Datei zu Dokumentationszwecken erstellt werden 

  - Die *.md Datei zur Dokumentation soll wie die Kerngrammatik heißen (wie die wichtigste Grammatik unter den Grammatiken der Sprache)

  - Diese Dokumentation dient nicht dazu, um Modellierern die Sprachen zu erklären, sondern um eine Dokumentation für Sprachentwickler bereitzustellen 

  - Für die Dokumentation, die an Modellierer gerichtet ist: individuell eine eigene geeignete Form nutzen

  - Die Grammatiken dokumentieren die abstrakte Syntax und die Symboltabelle

  - In der Grammatik sollen unter anderem Kommentare eingebaut werden, die z.B. Designentscheidungen begründen

  

- Inhalt der detaillierten Sprachdokus (Für Sprachentwickler): 

  - Zweck der Sprache

  - Durch welche handgeschriebenen Klassen wurde die abstrakte Syntax erweitert?

  - Was sind die wichtigsten (handgeschriebenen) internen Funktionalitäten 

    (Funktionen, die auf der abstrakten Syntax Informationen berechnen oder die abstrakte Syntax modifizieren), 

    z.B. Trafos, Symboltabellenberechnungen, CoCo checks

  - Welche Erweiterungspunkte für die Syntax sind vorgesehen? 

    (z.B. in Form von Top-Mechanismus/Pattern zur Erweiterung)

  - Welche Generatorfunktionalitäten existieren?

    (z.B. PrettyPrinter)

  - Welche Erweiterungspunkte für Generatoren sind vorgesehen?

  - Verwandte Sprachen/ benutzte Sprachen (wie?, weshalb?, warum?)
