package featureconfigurationpartial._symboltable;

public class FeatureConfigurationPartialLanguage extends FeatureConfigurationPartialLanguageTOP{
  public FeatureConfigurationPartialLanguage() {
    super("Partial Feature Model Configuration", "fc");
  }

  @Override
  protected FeatureConfigurationPartialModelLoader provideModelLoader() {
    return new FeatureConfigurationPartialModelLoader(this);
  }
}
