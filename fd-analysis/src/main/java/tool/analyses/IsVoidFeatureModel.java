/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import tool.transform.FZNModelBuilder;

import java.util.Collection;
import java.util.Map;

public class IsVoidFeatureModel extends Analysis<Boolean> {

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(configurations.isEmpty());
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(false);
  }

}
