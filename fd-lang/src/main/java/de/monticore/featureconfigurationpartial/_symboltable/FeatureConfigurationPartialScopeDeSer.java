/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial._symboltable;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.symboltable.serialization.json.JsonObject;

import static de.monticore.symboltable.serialization.JsonDeSers.NAME;
import static de.monticore.symboltable.serialization.JsonDeSers.PACKAGE;

/**
 * The serialization of partial feature configuration scopes relies on a global scope
 * to resolve for the feature diagram referenced from a PartialFC model.
 */
public class FeatureConfigurationPartialScopeDeSer
    extends FeatureConfigurationPartialScopeDeSerTOP {

  @Override protected IFeatureConfigurationPartialArtifactScope deserializeFeatureConfigurationPartialArtifactScope(
      JsonObject scopeJson) {
    String packageName = scopeJson
        .getStringMemberOpt(PACKAGE).orElse("");
    IFeatureConfigurationPartialArtifactScope scope = FeatureConfigurationPartialMill
        .featureConfigurationPartialArtifactScopeBuilder()
        .setPackageName(packageName)
        .build();
    FeatureConfigurationPartialMill.getFeatureConfigurationPartialGlobalScope().addSubScope(scope);
    if (scopeJson.hasStringMember(NAME)) {
      scope.setName(scopeJson.getStringMember(NAME));
    }
    scope.setExportingSymbols(true);

    deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    addSymbols(scopeJson, scope);
    return scope;
  }
}
