package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import tool.transform.trafos.config.ConfigurationTrafo;

import java.util.Collection;
import java.util.Map;

public class IsValid extends Analysis<Boolean> {

  private ASTFeatureConfiguration configuration;

  public IsValid(ASTFeatureConfiguration configuration) {
    this.configuration = configuration;
    this.builder.addFeatureModelFZNTrafo(new ConfigurationTrafo(configuration));
  }

  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(!configurations.isEmpty());
  }

}
