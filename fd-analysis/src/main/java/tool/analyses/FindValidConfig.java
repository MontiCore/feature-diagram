/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.Collection;
import java.util.Map;

public class FindValidConfig extends Analysis<ASTFeatureConfiguration> {
  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    if (configurations.isEmpty()) {
      setResult(null);
    }
    else {
      setResult(configurations.iterator().next());
    }
  }
}
