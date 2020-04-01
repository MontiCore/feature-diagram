package tool.transform.trafos;

import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootFeatureSelected implements FeatureModel2FlatZincModelTrafo {

  private FeatureDiagramSymbol featureModel;
  private List<Constraint> constraints = new ArrayList<>();

  @Override
  public void setNames(List<String> names) {
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;

  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return this.featureModel;
  }

  @Override
  public List<Constraint> getConstraints() {
    return constraints;
  }

  @Override
  public List<Variable> getVariables() {
    return Collections.emptyList();
  }

  @Override
  public void perform() {
    Constraint rootFeature = new Constraint("int_eq", "1", featureModel.getName());
    constraints.add(rootFeature);
  }
}
