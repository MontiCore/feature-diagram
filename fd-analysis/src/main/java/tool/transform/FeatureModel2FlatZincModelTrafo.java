/* (c) https://github.com/MontiCore/monticore */
package tool.transform;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import net.sourceforge.plantuml.Log;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;

public interface FeatureModel2FlatZincModelTrafo {

  public void setNames(List<String> names);

  public ASTFeatureDiagram getFeatureModel();

  public void setFeatureModel(ASTFeatureDiagram featureModel);

  public List<Constraint> getConstraints();

  public List<Variable> getVariables();

  public void perform();
}
