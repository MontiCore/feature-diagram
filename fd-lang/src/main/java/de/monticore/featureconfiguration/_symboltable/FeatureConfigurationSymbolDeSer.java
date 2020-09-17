/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class deserializes FeatureConfigurationSymbols. Especially, the attributes of the symbol of
 * the referenced  FD model and the FeatureSymbols are realized.
 */
public class FeatureConfigurationSymbolDeSer extends FeatureConfigurationSymbolDeSerTOP {

  FeatureDiagramSymbol fdSymbol;

  @Override public FeatureDiagramSymbol deserializeFeatureDiagram(JsonObject symbolJson,
      IFeatureConfigurationScope enclosingScope) {

    String fdName = symbolJson.getStringMember("featureDiagram");
    Optional<FeatureDiagramSymbol> featureDiagramSymbol = enclosingScope
        .resolveFeatureDiagram(fdName);

    if (featureDiagramSymbol.isPresent()) {
      fdSymbol = featureDiagramSymbol.get();
      return fdSymbol;
    }
    Log.error("0xFC646 Unable to find the FD '" + fdName
        + "' that the stored feature configuration '"
        + symbolJson + "' refers to!");

    return null;
  }

  @Override public List<FeatureSymbol> deserializeSelectedFeatures(JsonObject symbolJson,
      IFeatureConfigurationScope enclosingScope) {
    List<FeatureSymbol> result = new ArrayList<>();
    if (null == fdSymbol) {
      Log.error("0xFC6A9 Unable to find the feature diagram that the stored feature configuration '"
          + symbolJson + "' refers to!");
      return new ArrayList<>();
    }
    for (JsonElement f : symbolJson.getArrayMember("selectedFeatures")) {
      String feature = f.getAsJsonString().getValue();
      Optional<FeatureSymbol> featureSymbol = fdSymbol.getSpannedScope()
          .resolveFeatureLocally(feature);
      if (featureSymbol.isPresent()) {
        result.add(featureSymbol.get());
      }
      else {
        Log.error("0xFC649 Unable to find the feature '" + feature
            + "' that the stored feature configuration '" + symbolJson + "' refers to!");
      }
    }
    return result;
  }
}
