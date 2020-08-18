/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.Collection;

public class NumberOfProducts extends Analysis<Integer> {
  public NumberOfProducts() {
    super();
    builder.setAllSolutions(true);
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(configurations.size());
  }

}
