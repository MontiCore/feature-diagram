/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import featurediagram._symboltable.*;

import java.util.ArrayList;
import java.util.List;

public class FeatureSymbolDeSer extends FeatureSymbolDeSerTOP {

  @Override protected List<FeatureGroup> deserializeChildren(JsonObject symbolJson,
      IFeatureDiagramScope enclosingScope) {
    List<FeatureGroup> result = new ArrayList<>();
    String featureName = symbolJson.getStringMember("name");
    for (JsonElement e : symbolJson.getArrayMember("children")) {
      result.add(deserializeFeatureGroup(e.getAsJsonObject(), featureName, enclosingScope));
    }
    return result;
  }

  protected FeatureGroup deserializeFeatureGroup(JsonObject o, String parentFeatureName,
      IFeatureDiagramScope enclosingScope) {
    FeatureSymbolLoader parent = getLoader(parentFeatureName, enclosingScope);
    List<FeatureSymbolLoader> members = deserializeMembers(o, enclosingScope);
    GroupKind kind = GroupKind.valueOf(o.getStringMember("kind"));
    if (GroupKind.CARDINALITY == kind) {
      int min = o.getIntegerMember("min");
      int max = o.getIntegerMember("max");
      return new FeatureGroup(parent, members, min, max);
    }
    else {
      return new FeatureGroup(parent, members, kind);
    }
  }

  protected List<FeatureSymbolLoader> deserializeMembers(JsonObject o,
      IFeatureDiagramScope enclosingScope) {
    List<FeatureSymbolLoader> members = new ArrayList<>();
    for (JsonElement e : o.getArrayMember("members")) {
      if (e.isJsonString()) {
        String childName = e.getAsJsonString().getValue();
        members.add(getLoader(childName, enclosingScope));
      }
    }
    return members;
  }

  protected FeatureSymbolLoader getLoader(String name, IFeatureDiagramScope enclosingScope) {
    return FeatureDiagramSymTabMill.featureSymbolLoaderBuilder()
        .setName(name)
        .setEnclosingScope(enclosingScope)
        .build();
  }
}
