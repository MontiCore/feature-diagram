/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;


/**
 * This handwritten deser serializes and deserializes FeatureSymbols.
 * The serialization strategy for FeatureSymbols deviates from the generated strategy as it
 * stores all FeatureSymbols as a list of feature names that are a member of a stored
 * FeatureDiagramSymbol.
 */
public class FeatureDiagramSymbolDeSer extends FeatureDiagramSymbolDeSerTOP {

  protected static final String FEATURES = "features";

  @Override
  public String serialize(FeatureDiagramSymbol toSerialize,
                          FeatureDiagramSymbols2Json s2j) {
    JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(JsonDeSers.KIND, getSerializedKind());
    p.member(JsonDeSers.NAME, toSerialize.getName());

    p.beginObject(JsonDeSers.SPANNED_SCOPE);

    p.beginArray(JsonDeSers.SYMBOLS);

    for (int i = 0; i < toSerialize.getAllFeatures().size(); i++) {
      p.beginObject();
      p.member(JsonDeSers.KIND, s2j.featureSymbolDeSer.getSerializedKind());

      p.member(JsonDeSers.NAME, toSerialize.getAllFeatures().get(i).getName());
      p.endObject();
    }
    p.endArray();

    p.endObject();

    // do not serialize spanned scope, but list of feature names
    //p.array(FEATURES, toSerialize.getAllFeatures(), f -> ("\"" + f.getName() + "\""));
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }

  @Override
  public FeatureDiagramSymbol deserialize(JsonObject symbolJson) {
    FeatureDiagramSymbolBuilder builder = FeatureDiagramMill.featureDiagramSymbolBuilder();
    builder.setName(symbolJson.getStringMember(JsonDeSers.NAME));
    FeatureDiagramSymbol symbol = builder.build();

    // ignore deserializing serialized spanned scope, create and link new scope
    IFeatureDiagramScope fdScope = FeatureDiagramMill.scope();
    symbol.setSpannedScope(fdScope); //for bidirectional link

    // deserialize list of serialized feature names and add to scope
    if (symbolJson.hasArrayMember(FEATURES)) {
      for (JsonElement e : symbolJson.getArrayMember(FEATURES)) {
        FeatureSymbol featureSymbol = FeatureDiagramMill
            .featureSymbolBuilder()
            .setName(e.getAsJsonString().getValue())
            .setEnclosingScope(fdScope)
            .build();
        fdScope.add(featureSymbol);
      }
    }

    deserializeAddons(symbol, symbolJson);
    return symbol;
  }

}
