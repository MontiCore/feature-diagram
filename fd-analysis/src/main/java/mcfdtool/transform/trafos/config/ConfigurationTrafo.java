/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos.config;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.FeatureModel2FlatZincModelTrafo;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.Variable;
import mcfdtool.visitors.CompleteConfigToPartialConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationTrafo
    implements FeatureModel2FlatZincModelTrafo {

  private Map<String, Boolean> configuration;

  private ASTFeatureDiagram feature;

  private List<Constraint> constraints = new ArrayList<>();

  private List<Variable> variables = new ArrayList<>();

  private ASTFeatureConfiguration astFeatureConfiguration;

  private boolean undefinedEqualsNot;

  public ConfigurationTrafo(ASTFeatureConfiguration configuration) {
    this(configuration, true);
  }

  public ConfigurationTrafo(ASTFeatureConfiguration configuration, boolean undefinedEqualsNot) {
    this.astFeatureConfiguration = configuration;
    this.undefinedEqualsNot = undefinedEqualsNot;
  }

  @Override
  public void setNames(List<String> names) {

  }

  @Override
  public ASTFeatureDiagram getFeatureModel() {
    return this.feature;
  }

  @Override
  public void setFeatureModel(ASTFeatureDiagram featureModel) {
    this.feature = featureModel;
  }

  @Override
  public List<Constraint> getConstraints() {
    return constraints;
  }

  @Override
  public List<Variable> getVariables() {
    return variables;
  }

  @Override
  public void perform() {
    configuration = CompleteConfigToPartialConfig
        .getConfiguration(astFeatureConfiguration, feature, undefinedEqualsNot);
    configuration.forEach((name, isSelected) -> {
      if (isSelected == null) {
        return;
      }
      if (isSelected) {
        constraints.add(new Constraint("int_eq", "1", name));
      }
      if (!isSelected) {
        constraints.add(new Constraint("int_eq", name, "0"));
      }
    });
  }
}
