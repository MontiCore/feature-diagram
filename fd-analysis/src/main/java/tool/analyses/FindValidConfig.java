/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.Collection;

public class FindValidConfig extends Analysis<ASTFeatureConfiguration> {
  
  // TODO: Ernsthaft? This should be really optimized to not produce all and then select only one
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
