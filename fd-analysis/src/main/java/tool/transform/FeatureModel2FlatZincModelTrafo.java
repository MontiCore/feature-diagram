/* (c) https://github.com/MontiCore/monticore */
package tool.transform;

import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.List;

public interface FeatureModel2FlatZincModelTrafo {

  public void setNames(List<String> names);

  public FeatureDiagramSymbol getFeatureModel();

  public void setFeatureModel(FeatureDiagramSymbol featureModel);

  public List<Constraint> getConstraints();

  public List<Variable> getVariables();

  public void perform();
}
