/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import mcfdtool.transform.trafos.config.ConfigurationTrafo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Filter extends Analysis<Set<ASTFeatureConfiguration>> {
  public Filter(ASTFeatureConfiguration configuration) {
    super();
    super.getModelBuilder().addFeatureModelFZNTrafo(new ConfigurationTrafo(configuration, false));
    builder.setAllSolutions(true);
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(new HashSet<>(configurations));
  }
}