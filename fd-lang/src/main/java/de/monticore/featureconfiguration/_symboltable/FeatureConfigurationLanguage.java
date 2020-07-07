/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._symboltable;

/**
 * Language for feature configurations
 */
public class FeatureConfigurationLanguage extends FeatureConfigurationLanguageTOP {

  public FeatureConfigurationLanguage() {
    super("Feature Configuration", "fc");
  }

  @Override
  protected FeatureConfigurationModelLoader provideModelLoader() {
    return new FeatureConfigurationModelLoader(this);
  }

}
