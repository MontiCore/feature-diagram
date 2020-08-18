/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.Variable;

import java.util.List;

public interface FeatureModel2FlatZincModelTrafo {

  public void setNames(List<String> names);

  public ASTFeatureDiagram getFeatureModel();

  public void setFeatureModel(ASTFeatureDiagram featureModel);

  public List<Constraint> getConstraints();

  public List<Variable> getVariables();

  public void perform();
}
