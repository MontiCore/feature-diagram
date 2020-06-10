/* (c) https://github.com/MontiCore/monticore */
package tool.transform.trafos.config;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;
import tool.util.CompleteConfigToPartialConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationTrafo
    implements FeatureModel2FlatZincModelTrafo {

  private Map<String, Boolean> configuration;

  private FeatureDiagramSymbol feature;

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
  public FeatureDiagramSymbol getFeatureModel() {
    return this.feature;
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
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
        constraints.add(new Constraint("int_le", "1", name));
      }
      if (!isSelected) {
        constraints.add(new Constraint("int_le", name, "0"));
      }
    });
  }
}
