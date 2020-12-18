/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial._symboltable;

import de.monticore.featureconfiguration._symboltable.FeatureDiagramResolver;

/**
 * This class adds the resolver for feature diagram symbols directly in the constructor to
 * have it set regardless of any mill reconfigurations
 */
public class FeatureConfigurationPartialGlobalScope extends FeatureConfigurationPartialGlobalScopeTOP {

  protected FeatureConfigurationPartialGlobalScope realThis = this;

  public FeatureConfigurationPartialGlobalScope() {
    this.addAdaptedFeatureDiagramSymbolResolver(new FeatureDiagramResolver());
    setFileExt("fc");
  }

  @Override public FeatureConfigurationPartialGlobalScope getRealThis() {
    return realThis;
  }

  public void setRealThis(FeatureConfigurationPartialGlobalScope realThis) {
    this.realThis = realThis;
  }
}
