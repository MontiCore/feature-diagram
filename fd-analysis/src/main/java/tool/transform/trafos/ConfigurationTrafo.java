package tool.transform.trafos;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._visitor.HierachicalFeatureSymbolVisitor;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;
import tool.util.CompleteConfigToPartialConfig;
import tool.util.FeatureMinMaxCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurationTrafo implements FeatureModel2FlatZincModelTrafo, HierachicalFeatureSymbolVisitor {

  private Map<String, Boolean> configuration;
  private FeatureDiagramSymbol feature;
  private List<Constraint> constraints = new ArrayList<>();
  private List<Variable> variables = new ArrayList<>();
  private ASTFeatureConfiguration astFeatureConfiguration;

  public ConfigurationTrafo(ASTFeatureConfiguration configuration) {
    this.astFeatureConfiguration = configuration;
  }

  @Override
  public void setNames(List<String> names) {

  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.feature = featureModel;
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return this.feature;
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
    configuration = CompleteConfigToPartialConfig.getConfiguration(astFeatureConfiguration, feature, false);
    FeatureMinMaxCalculator calculator = new FeatureMinMaxCalculator();
    getFeatureModel().accept(calculator);
    configuration.forEach((name, isSelected)->{
      if(isSelected == null){return;}
      if(isSelected){
        constraints.add(new Constraint("int_le", "1", name));
      }
      if(!isSelected){
        constraints.add(new Constraint("int_le", name, "0"));
      }
    });
  }
}
