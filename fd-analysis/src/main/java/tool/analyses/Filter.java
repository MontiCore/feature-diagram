/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import tool.transform.trafos.config.ConfigurationTrafo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Filter extends Analysis<Set<ASTFeatureConfiguration>> {
  public Filter(ASTFeatureConfiguration configuration) {
    super();
    super.getModelBuilder().addFeatureModelFZNTrafo(new ConfigurationTrafo(configuration));
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(new HashSet<>(configurations));
  }
}
