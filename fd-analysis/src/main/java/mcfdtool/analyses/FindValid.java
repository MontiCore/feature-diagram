/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.Map;

/**
 * This analysis returns any configuration that is valid w.r.t. the passed FD.
 * If no such configuration exists, the analysis returns "null"
 */
public class FindValid {

  public ASTFeatureConfiguration perform(ASTFeatureDiagram fd) {
    FlatZincModel model = FlatZincTrafo.getInstance().addFeatureDiagram(fd).build();
    Map<String, Integer> anySolution = Solvers.getSolver().getAnySolution(model);
    return Solvers.transformResultToFC("ValidConfiguration", anySolution, fd);
  }

}
