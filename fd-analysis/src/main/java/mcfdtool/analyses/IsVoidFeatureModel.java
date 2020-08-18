/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import mcfdtool.transform.FZNModelBuilder;

import java.util.Collection;

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
