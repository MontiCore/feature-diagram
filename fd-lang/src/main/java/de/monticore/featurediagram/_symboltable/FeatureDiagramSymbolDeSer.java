/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * This handwritten deser serializes and deserializes FeatureSymbols.
 * The serialization strategy for FeatureSymbols  * deviates from the generated strategy as it
 * stores all FeatureSymbols as a list of feature names that are a member of a stored
 * FeatureDiagramSymbol.
 */
public class FeatureDiagramSymbolDeSer extends FeatureDiagramSymbolDeSerTOP {

  @Override protected void serializeAddons(FeatureDiagramSymbol toSerialize,
      FeatureDiagramSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.array("features", toSerialize.getAllFeatures(), f -> ("\"" + f.getName() + "\""));
  }

  @Override protected void deserializeAddons(FeatureDiagramSymbol symbol, JsonObject symbolJson) {
    IFeatureDiagramScope fdScope = FeatureDiagramMill.scope();
    symbol.setSpannedScope(fdScope); //for bidirectional link
    symbol.getEnclosingScope().addSubScope(fdScope); //for bidirectional link

    if (symbolJson.hasArrayMember("features")) {
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

}
