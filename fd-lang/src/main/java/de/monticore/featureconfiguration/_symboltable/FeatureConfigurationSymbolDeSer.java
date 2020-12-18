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

import static de.monticore.featurediagram._symboltable.FeatureModelImporter.loadFeatureModel;

/**
 * This class deserializes FeatureConfigurationSymbols. Especially, the attributes of the symbol of
 * the referenced FD model and the FeatureSymbols are realized.
 */
public class FeatureConfigurationSymbolDeSer extends FeatureConfigurationSymbolDeSerTOP {

  protected FeatureDiagramSymbol fdSymbol;

  /**
   * serializes the feature diagram as qualified name in form of a JSON String
   * @param featureDiagram
   * @param s2j
   */
  @Override protected void serializeFeatureDiagram(FeatureDiagramSymbol featureDiagram,
      FeatureConfigurationSymbols2Json s2j) {
    s2j.getJsonPrinter().member("featureDiagram", featureDiagram.getFullName());
  }

  /**
   * serializes the list of feature symbols as JSON array of the (unqualified) feature names
   * as JSON Strings
   * @param selectedFeatures
   * @param s2j
   */
  @Override protected void serializeSelectedFeatures(List<FeatureSymbol> selectedFeatures,
      FeatureConfigurationSymbols2Json s2j) {
    s2j.getJsonPrinter().array("selectedFeatures", selectedFeatures,
        feature -> "\"" + feature.getName() + "\"");
  }

  @Override public FeatureDiagramSymbol deserializeFeatureDiagram(JsonObject symbolJson) {

    String fdName = symbolJson.getStringMember("featureDiagram");
    fdSymbol = loadFeatureModel(fdName, symbolJson.toString());

    if (null == fdSymbol) {
      Log.error("0xFC646 Unable to find the FD '" + fdName
          + "' that the stored feature configuration '"
          + symbolJson + "' refers to!");
    }

    return fdSymbol;
  }

  @Override public List<FeatureSymbol> deserializeSelectedFeatures(JsonObject symbolJson) {
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
