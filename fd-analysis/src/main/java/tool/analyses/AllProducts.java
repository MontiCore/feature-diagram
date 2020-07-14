/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AllProducts extends Analysis<Set<ASTFeatureConfiguration>> {

  public AllProducts() {
    super();
    builder.setAllSolutions(true);
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(new HashSet<>(configurations));
  }

}
