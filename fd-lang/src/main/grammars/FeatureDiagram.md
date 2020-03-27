<!-- (c) https://github.com/MontiCore/monticore -->
# Feature Diagram Languages in MontiCore

The feature diagram languages comprise two grammars: 
* The **[FeatureDiagram grammar](fd-lang/src/main/grammars/FeatureDiagram.mc4)** describes the syntax
of feature models. It contains several extension points that can be used to tailor the language to 
different applications. For instance, it is possible to add feature attributes.
* The **[FeatureConfiguration grammar](fd-lang/src/main/grammars/FeatureDiagram.mc4)** describes the syntax
of feature configurations. As feature configurations always exist in the context of a feature model, this 
grammar reuses symbols from the FeatureDiagram language.


