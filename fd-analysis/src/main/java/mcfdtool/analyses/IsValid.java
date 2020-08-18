/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import mcfdtool.transform.trafos.config.ConfigurationTrafo;

import java.util.Collection;

public class IsValid extends Analysis<Boolean> {

  private ASTFeatureConfiguration configuration;

  public IsValid(ASTFeatureConfiguration configuration) {
    this.configuration = configuration;
    this.builder.addFeatureModelFZNTrafo(new ConfigurationTrafo(configuration, true));
  }

  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(!configurations.isEmpty());
  }

}