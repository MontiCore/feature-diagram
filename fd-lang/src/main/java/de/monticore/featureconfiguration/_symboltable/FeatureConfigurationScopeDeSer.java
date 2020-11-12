/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.symboltable.serialization.json.JsonObject;

import static de.monticore.symboltable.serialization.JsonDeSers.NAME;
import static de.monticore.symboltable.serialization.JsonDeSers.PACKAGE;

/**
 * The serialization of feature configuration scopes relies on a global scope
 * to resolve for the feature diagram referenced from an FC model.
 */
public class FeatureConfigurationScopeDeSer extends FeatureConfigurationScopeDeSerTOP {

  @Override protected IFeatureConfigurationArtifactScope deserializeFeatureConfigurationArtifactScope(
      JsonObject scopeJson) {
    String packageName = scopeJson
        .getStringMemberOpt(PACKAGE).orElse("");
    IFeatureConfigurationArtifactScope scope = FeatureConfigurationMill
        .featureConfigurationArtifactScopeBuilder()
        .setPackageName(packageName)
        .build();
    FeatureConfigurationMill.featureConfigurationGlobalScope().addSubScope(scope);
    if (scopeJson.hasStringMember(NAME)) {
      scope.setName(scopeJson.getStringMember(NAME));
    }
    scope.setExportingSymbols(true);

    deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    addSymbols(scopeJson, scope);
    return scope;
  }
}
