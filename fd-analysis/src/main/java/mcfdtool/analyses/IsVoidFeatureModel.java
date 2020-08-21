/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

/**
 * This analysis returns true, if the passed FD has a valid configuration and false otherwise
 */
public class IsVoidFeatureModel {

  public Boolean perform(ASTFeatureDiagram fd) {
    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd).build();
    return !Solvers.getSolver().hasSolution(model);
  }

}
