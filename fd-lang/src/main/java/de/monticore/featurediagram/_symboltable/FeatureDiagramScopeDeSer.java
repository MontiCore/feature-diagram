/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  public FeatureDiagramScopeDeSer() {
    this.setSymbolFileExtension("fdsym");
  }

  @Override protected void deserializeFeatureDiagramSymbol(JsonObject symbolJson,
      IFeatureDiagramScope scope) {
    FeatureDiagramSymbol symbol = featureDiagramSymbolDeSer
        .deserializeFeatureDiagramSymbol(symbolJson, scope);
    scope.add(symbol);

    IFeatureDiagramScope fdScope = symbol.getSpannedScope();
    List<FeatureSymbol> featureSymbols = new ArrayList<>();
    for (JsonElement e : symbolJson.getArrayMember("features")) {
      FeatureSymbol featureSymbol = FeatureDiagramMill
          .featureSymbolBuilder()
          .setName(e.getAsJsonString().getValue())
          .setEnclosingScope(fdScope)
          .build();
      featureSymbols.add(featureSymbol);
    }
  }
}
