<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

<!-- List with all references used within this markdown file: -->
[Grammar]:                   ../../../../../../../../fd-lang/src/main/grammars/de/monticore/FeatureConfiguration.mc4
[fcstc]:                     ../../../../../../../../fd-lang/src/main/java/de/monticore/featureconfiguration/_symboltable/FeatureConfigurationSymbolTableCreator.java
[tool]:                      ../../../../../../../../fd-lang/src/main/java/de/monticore/featureconfiguration/FeatureConfigurationTool.java

[PartialGrammar]:                   ../../../../../../../../fd-lang/src/main/grammars/de/monticore/FeatureConfigurationPartial.mc4
[pfcstc]:                     ../../../../../../../../fd-lang/src/main/java/de/monticore/featureconfigurationpartial/_symboltable/FeatureConfigurationPartialSymbolTableCreator.java
[tool]:                      ../../../../../../../../fd-lang/src/main/java/de/monticore/featureconfigurationpartial/FeatureConfigurationPartialTool.java
[UseSelectBlockCoCo]:                      ../../../../../../../../fd-lang/src/main/java/de/monticore/featureconfigurationpartial/_cocos/UseSelectBlock.java

[Readme]:                    ../../../../../../../../README.md
[clitool]:                   ../../../../../../../../fd-analysis/src/main/java/tool/FACT.java
[FeatureDiagram MLC]: FeatureDiagram.md
[BasicSymbols MLC]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/BasicSymbols.mc4

> NOTE: <br>
This document is intended for  **language engineers** who extend, adapt or embed the FC language.
**modelers** please look **[here][Readme]**. 

# MontiCore Feature Configuration Languages

[[_TOC_]]

This project contains two alternative languages for modeling feature configurations.
The language for feature configurations enables modeling feature configurations that select
features of a feature model. The language for partial feature configurations additionally
enables to explicitly exclude features from a feature configuration.

### The Language for Feature Configurations

The following documents the MontiCore feature configuration language. 
Feature configurations (FCs) select features from a feature model. 
Hence, a FC model always exists in the context of a specific feature model.

The FC language is extensible and adaptible to tailor it for various different 
applications.

### Syntax Example
```
/* (c) https://github.com/MontiCore/monticore */
 import fdvalid.CarNavigation;
 
 featureconfig BasicCarNavigation for CarNavigation {
   CarNavigation, VoiceControl, Display, SmallScreen, GPS, Memory, Small;
 }
```
The configuration `BasicCarNavigation` refers to the feature diagram `CarNavigation`
and from this, selects seven features. It does not make any assertions about other
features of `CarNavigation`. 
For a detailed explanation of the meaning, please have a look at 
**[the readme][Readme]**.

### Syntax
#### Grammar
The syntax of the FC language is specified through the 
[FeatureConfiguration grammar][Grammar] that is itself a 
documentation of the concrete and abstract syntax. The grammar
extends the **[de.monticore.FeatureDiagram][FeatureDiagram MLC]**
grammar to reuse the definitions of `FeatureDiagramSymbols` and 
`FeatureSymbols`.

The grammar contains an extension point that can be used to tailor the language to 
different applications. For instance, it is possible to realize partial feature 
configurations that distinguish explicitly excluded features from those for which
no selection has been made yet.
* The interface nonterminal **FCElement** can be implemented to add further 
elements to the FC's body.

For realizing the FC language, it was not necessary to implement handwritten
extensions of AST classes, symbol classes, or the scope class.

#### Symboltable
- De-/Serialization functionality for the symbol table of the FC language does not exist,
  because (to the best of our knowledge) there is no use case for which this is beneficial.
   
- The [`FeatureConfigurationSymbolTableCreator`][fcstc] handles the creation and linking of the
  symbols after the FC is parsed. It creates a `FeatureConfigurationSymbol` and loads the 
  referenced `FeatureDiagramSymbol` and te `FeatureSymbols` of selected features. 


#### Symbol kinds used by FC (importable):
- An FC (as defined here) imports `FeatureDiagramSymbols` and `FeatureSymbols` 
  from the [FeatureDiagram][FeatureDiagram MLC] language. These symbols are used
  to check whether the feature diagram name and feature names used in an FC model
  are defined in an FD model. For performing more sophisticacted analyses on an FC
  (as described in the [Readme][Readme]) loading stored symbols of the FD language 
  is not sufficient. For these, the FD model has to be parsed.  

#### Symbol kinds defined by FC (exported):
 - For each FC there is a `FeatureConfigurationSymbol` defined as:
  ```
  class FeatureConfigurationSymbol {
      String name;
      /FeatureDiagramSymbol featureDiagram;
      /List<FeatureSymbol> selectedFeatures;
  }
  ```
  The FC language does not reuse the `DiagramSymbol` of the 
  [BasicSymbols][BasicSymbols MLC] language component. This is due to the fact 
  that `FeatureConfigurationSymbols` have attributes of the feature diagram and the selected 
  features that the `DiagramSymbol` does not provide.

#### Symbols exported by FC in the stored symboltable:
No symbols are exported from an FC model. 

#### Context Conditions
The feature configuration language does not define any CoCo classes.
The existance of the feature diagram referred from an FC model is 
checked during symbol table creation.
Similarly, checking whether the features selected in an FC 
exist in the referenced feature diagram is performed during symbol 
table creation.



### Generator and Supported Feature Analyses
For a description of the generator and feature analyses, please have a look 
at **[the FeatureDiagram description][FeatureDiagram MLC]**. 

<!-- %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% -->
___
___

### The Language for Partial Feature Configurations

The following documents the MontiCore partial feature configuration language. 
Partial feature configurations select and exclude features from a feature model. 
Compared to FC models as described above, partial feature configurations distinguish
features, for which no decision has not (yet) been made from features that are explicitly
excluded in a configuration. This can be used, e.g., for staged configuration processes in 
software product line engineering.


### Syntax Example
```
/* (c) https://github.com/MontiCore/monticore */
 import fdvalid.CarNavigation;
 
 featureconfig BasicCarNavigation2 for CarNavigation {
   select { CarNavigation, VoiceControl, Display, GPS, Memory, Small }
   exclude { LargeScreen, PreinstalledMaps }
 }
```
The configuration `BasicCarNavigation` refers to the feature diagram `CarNavigation`
and from this, selects six features. It further excludes two features from the 
configuration. It does not make any assertions about other
features of `CarNavigation`. 
For a detailed explanation of the meaning, please have a look at 
**[the readme][Readme]**.

### Syntax
#### Grammar
The syntax of the partial FC language is specified through the 
**[FeatureConfigurationPartial grammar][PartialGrammar]** that is itself a 
documentation of the concrete and abstract syntax. The grammar
extends the **[FeatureConfiguration][Grammar]**
at its extension point `FCElement` and reuses the start production `FCCompilationUnit`.
It adds two nonterminals for realizing blocks of selected features and for realizing 
excluded features.

#### Symboltable
- De-/Serialization functionality for the symbol table of the language does not exist,
  because (to the best of our knowledge) there is no use case for which this is beneficial.
   
- The [`FeatureConfigurationPartialSymbolTableCreator`][pfcstc] handles the creation and linking of the
  symbols after the FC is parsed. It creates a `FeatureConfigurationSymbol` and loads the 
  referenced `FeatureDiagramSymbol` and te `FeatureSymbols` of selected features. 


#### Symbol kinds used by partial FC (importable):
- An FC (as defined here) imports `FeatureDiagramSymbols` and `FeatureSymbols` 
  from the [FeatureDiagram][FeatureDiagram MLC] language and `FeatureConvigurationSymbols`
  from the [FeatureConfiguration][Grammar] language. The symbols are used
  to check whether the feature diagram name and feature names used in an FC model
  are defined in an FD model. For performing more sophisticacted analyses on an FC
  (as described in the [Readme][Readme]) loading stored symbols of the FD language 
  is not sufficient. For these, the FD model has to be parsed.  

#### Symbol kinds defined by partial FC (exported):
 - For each partial FC there is a FeatureConfigurationSymbol defined as:
  ```
  class FeatureConfigurationSymbol {
      String name;
      /FeatureDiagramSymbol featureDiagram;
      /List<FeatureSymbol> selectedFeatures;
  }
  ```

#### Symbols exported by partial FC in the stored symboltable:
No symbols are exported from an FC model. 

#### Context Conditions
The existance of the feature diagram referred from a partial FC model is 
checked during symbol table creation.
Similarly, checking whether the features selected in a partial FC 
exist in the referenced feature diagram is performed during symbol 
table creation.
Further, the partial FC language contains a context condition to prohibit using lists of 
features outside of select and exclude blocks as inherited from the [FeatureConfiguration][Grammar] 
language. In a partial FC, the meaning of such lists is unclear. 



### Generator and Supported Feature Analyses
For a description of the generator and feature analyses, please have a look 
at **[the FeatureDiagram description][FeatureDiagram MLC]**. 

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

