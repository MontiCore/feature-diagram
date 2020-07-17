/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This handwritten scope deser ensures that a symbolFileExtension has been set.
 * Further, it deserializes the FeatureSymbols. The serialization strategy for FeatureSymbols
 * deviates from the generated strategy as it stores all FeatureSymbols as a list of feature names
 * that are a member of a stored FeatureDiagramSymbol.
 */
public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  public FeatureDiagramScopeDeSer() {
    this.setSymbolFileExtension("fdsym");
  }

  @Override protected void deserializeFeatureDiagramSymbol(JsonObject symbolJson,
      IFeatureDiagramScope scope) {
    FeatureDiagramSymbol symbol = featureDiagramSymbolDeSer
        .deserializeFeatureDiagramSymbol(symbolJson, scope);

    IFeatureDiagramScope fdScope = FeatureDiagramMill.featureDiagramScopeBuilder().build();
    symbol.setSpannedScope(fdScope); //for bidirectional link
    scope.addSubScope(fdScope); //for bidirectional link
    scope.add(symbol);

    for (JsonElement e : symbolJson.getArrayMember("features")) {
      FeatureSymbol featureSymbol = FeatureDiagramMill
          .featureSymbolBuilder()
          .setName(e.getAsJsonString().getValue())
          .setEnclosingScope(fdScope)
          .build();
      fdScope.add(featureSymbol);
    }

  }
}