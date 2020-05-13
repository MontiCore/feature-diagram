/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonObject;
import featurediagram.FeatureDiagramMill;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

public class FeatureDiagramSymbolDeSer extends FeatureDiagramSymbolDeSerTOP {

  @Override protected FeatureSymbol deserializeRootFeature(JsonObject symbolJson,
                                                           IFeatureDiagramScope enclosingScope) {
    return FeatureDiagramMill.featureSymbolBuilder()
        .setName(symbolJson.getStringMember("rootFeature"))
        .setEnclosingScope(enclosingScope)
        .build();
  }
}
