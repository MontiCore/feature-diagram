/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._visitor.UnSelectedFeatureCollector;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains some static methods for the realization of FD analyses
 */
public class FDAnalyses {

  /**
   * Counts the number of occurrences per feature in every valid configuration of the passed FD
   *
   * @param ast
   * @return
   */
  public static Map<String, Integer> countOccurrencesInFCs(ASTFeatureDiagram ast) {
    FlatZincModel model = FlatZincTrafo.getInstance().addFeatureDiagram(ast).build();
    List<Map<String, Integer>> allSolutions = Solvers.getSolver().getAllSolutions(model);
    List<ASTFeatureConfiguration> allConfigurations = Solvers
        .transformResultToFC("intermediateResult", allSolutions, ast);
    return countOccurrencesInFCs(allConfigurations);
  }

  /**
   * Counts the number of occurrences per feature in every passed configuration
   *
   * @param allConfigurations
   * @return
   */
  public static Map<String, Integer> countOccurrencesInFCs(
      List<ASTFeatureConfiguration> allConfigurations) {
    Map<String, Integer> occurrences = new HashMap<>();
    for (ASTFeatureConfiguration cfg : allConfigurations) {
      for (String feature : UnSelectedFeatureCollector.getSelectedFeatures(cfg)) {
        int currentOccurences = occurrences.containsKey(feature) ? occurrences.get(feature) : 0;
        occurrences.put(feature, ++currentOccurences);
      }
    }
    return occurrences;
  }

}
