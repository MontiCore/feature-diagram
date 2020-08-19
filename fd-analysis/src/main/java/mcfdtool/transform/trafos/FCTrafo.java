/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;

import java.util.List;

/**
 * This class transforms a feature configuration into a flatzinc model
 */
public class FCTrafo implements FeatureConfigurationPartialVisitor {

  protected FlatZincModel flatZincModel;

  public void apply(ASTFeatureConfiguration fc, FlatZincModel result) {
    flatZincModel = result;
    //run the visitor to add constraints for selected features
    fc.accept(this);
  }

  @Override
  public void visit(ASTFeatures node) {
    for (String fName : node.getNamesList()) {
      flatZincModel.add(new Constraint("int_eq", fName, "1"));
    }
  }

  @Override
  public void visit(ASTSelect node) {
    for (String fName : node.getNamesList()) {
      flatZincModel.add(new Constraint("int_eq", fName, "1"));
    }
  }

  @Override
  public void visit(ASTUnselect node) {
    for (String fName : node.getNamesList()) {
      flatZincModel.add(new Constraint("int_eq", fName, "0"));
    }
  }
}
