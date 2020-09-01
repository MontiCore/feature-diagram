/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.List;
import java.util.Map;

/**
 * Returns all configurations of a passed FD that satisfy a passed list of constraints
 *  !! WARNING: This analysis can be slow for large FDs !!
 */
public class GeneralFilter {

  public List<ASTFeatureConfiguration> perform(ASTFeatureDiagram fd, List<Constraint> filters) {
    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd).build();
    model.addConstraints(filters);
    List<Map<String, Integer>> allSolutions = Solvers.getSolver().getAllSolutions(model);
    return Solvers.transformResultToFC("CompletedConfiguration", allSolutions, fd);
  }

}
