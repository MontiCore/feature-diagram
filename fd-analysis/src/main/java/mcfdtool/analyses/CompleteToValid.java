/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.Map;

/**
 * This analysis completes the passed FC to any configuration that is valid w.r.t. the passe FD.
 * If no such configuration exists, the analysis returns "null"
 */
public class CompleteToValid {

  public ASTFeatureConfiguration perform(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd)._addFeatureConfiguration(fc).build();
    Map<String, Integer> anySolution = Solvers.getSolver().getAnySolution(model);
    return Solvers.transformResultToFC("CompletedConfiguration", anySolution, fd);
  }
}
