# MontiCore Feature Configuration Language




The feature diagram languages comprise two grammars: 
* The **[FeatureDiagram grammar](fd-lang/src/main/grammars/FeatureDiagram.mc4)** describes the syntax
of feature models. It contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.
* The **[FeatureConfiguration grammar](fd-lang/src/main/grammars/FeatureDiagram.mc4)** describes the syntax
of feature configurations. As feature configurations always exist in the context of a feature model, this 
grammar reuses symbols from the FeatureDiagram language.


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
