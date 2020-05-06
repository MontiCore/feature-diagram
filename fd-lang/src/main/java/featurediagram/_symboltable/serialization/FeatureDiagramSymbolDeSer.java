/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonObject;
import featurediagram._symboltable.FeatureDiagramSymTabMill;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.FeatureSymbolLoader;
import featurediagram._symboltable.IFeatureDiagramScope;

public class FeatureDiagramSymbolDeSer extends FeatureDiagramSymbolDeSerTOP {

  @Override protected FeatureSymbol deserializeRootFeature(JsonObject symbolJson,
                                                           IFeatureDiagramScope enclosingScope) {
    return FeatureDiagramSymTabMill.featureSymbolBuilder()
        .setName(symbolJson.getStringMember("rootFeature"))
        .setEnclosingScope(enclosingScope)
        .build();
  }
}
