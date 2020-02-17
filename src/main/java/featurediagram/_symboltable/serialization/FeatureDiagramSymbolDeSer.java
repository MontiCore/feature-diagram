/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonObject;
import featurediagram._symboltable.FeatureDiagramSymTabMill;
import featurediagram._symboltable.FeatureSymbolLoader;
import featurediagram._symboltable.IFeatureDiagramScope;

public class FeatureDiagramSymbolDeSer extends FeatureDiagramSymbolDeSerTOP {

  @Override protected FeatureSymbolLoader deserializeRootFeature(JsonObject symbolJson,
      IFeatureDiagramScope enclosingScope) {
    return FeatureDiagramSymTabMill.featureSymbolLoaderBuilder()
        .setName(symbolJson.getStringMember("rootFeature"))
        .setEnclosingScope(enclosingScope)
        .build();
  }
}
