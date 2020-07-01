/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import featurediagram.FeatureDiagramMill;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

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
