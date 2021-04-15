/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.List;
import java.util.Map;

/**
 * This analysis for finds all valid configurations (=products) of a passed FD.
 * !! WARNING: This analysis can be slow for large FDs !!
 */
public class AllProducts {

  public List<ASTFeatureConfiguration> perform(ASTFeatureDiagram fd) {
    FlatZincModel model = FlatZincTrafo.getInstance().addFeatureDiagram(fd).build();
    List<Map<String, Integer>> allSolutions = Solvers.getSolver().getAllSolutions(model);
    return Solvers.transformResultToFC("Configuration", allSolutions, fd);
  }
}
