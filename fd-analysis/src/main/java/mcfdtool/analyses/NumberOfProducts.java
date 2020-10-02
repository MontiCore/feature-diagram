/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.List;
import java.util.Map;

/**
 * This analysis returns the number of valid configurations (=products) of the
 * passed FD.
 * !! WARNING: This analysis can be slow for large FDs !!
 */
public class NumberOfProducts {

  public Integer perform(ASTFeatureDiagram fd) {
    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd).build();

    //each solution is a valid configuration
    List<Map<String, Integer>> allSolutions = Solvers.getSolver().getAllSolutions(model);
    if (null != allSolutions) {
      return allSolutions.size();
    }
    return null;
  }

}
