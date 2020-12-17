/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

/**
 * This class adds the resolver for feature diagram symbols directly in the constructor to
 * have it set regardless of any mill reconfigurations
 */
public class FeatureConfigurationGlobalScope extends FeatureConfigurationGlobalScopeTOP {

  protected FeatureConfigurationGlobalScope realThis = this;

  public FeatureConfigurationGlobalScope() {
    this.addAdaptedFeatureDiagramSymbolResolver(new FeatureDiagramResolver(this.getModelPath()));
  }

  @Override public FeatureConfigurationGlobalScope getRealThis() {
    return realThis;
  }

  public void setRealThis(FeatureConfigurationGlobalScope realThis) {
    this.realThis = realThis;
  }
}
