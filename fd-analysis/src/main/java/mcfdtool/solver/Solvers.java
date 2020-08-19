/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.solver;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationBuilder;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelectBuilder;
import de.monticore.featureconfigurationpartial._ast.ASTUnselectBuilder;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Solvers {

  private static ISolver SOLVER = null;

  public static ISolver getSolver() {
    if (null == SOLVER) {
      SOLVER = createDefaultSolver();
    }
    return SOLVER;
  }

  public static ISolver createDefaultSolver() {
    return new ChocoSolver();
  }

  public static void useDefaultSolver() {
    SOLVER = createDefaultSolver();
  }

  public static void setSolver(ISolver solver) {
    Solvers.SOLVER = solver;
  }

  /**
   * Transforms a passed solver result (a map of variables with their valuations) and a passed
   * list of feature names into a feature configuration. This works under the assumption that
   * for each feature, an integer variable exists in the set of variables in the solver result.
   * If the result is null, this method returns null
   *
   * @param result
   * @param fd
   * @return
   */
  public static ASTFeatureConfiguration transformResultToFC(String resultName,
      Map<String, Integer> result,
      ASTFeatureDiagram fd) {
    if (null == result) {
      return null;
    }
    ASTFeatureConfigurationBuilder builder = FeatureConfigurationPartialMill
        .featureConfigurationBuilder();
    ASTSelectBuilder selectBuilder = FeatureConfigurationPartialMill.selectBuilder();
    ASTUnselectBuilder unselectBuilder = FeatureConfigurationPartialMill.unselectBuilder();
    for (String feature : fd.getAllFeatures()) {
      if (!result.containsKey(feature)) {
        Log.error(
            "0xFD567 The feature '" + feature + "' does not occur in the result of the solver!");
      }
      else {
        boolean isSelected =
            result.get(feature) != 0; // the feature is selected, if its valuation is not 0
        if (isSelected) {
          selectBuilder.addNames(feature);
        }
        else {
          unselectBuilder.addNames(feature);
        }
      }
    }
    return builder
        .setName(resultName)
        .setFdName(fd.getName())
        .addFCElements(selectBuilder.build())
        .addFCElements(unselectBuilder.build())
        .build();
  }

  /**
   * Transforms a passed list of solver results (a list of maps of variables with their valuations) and a passed
   * list of feature names into a feature configuration. This works under the assumption that
   * for each feature, an integer variable exists in the set of variables in the solver result.
   * If a result is null, this method returns null
   *
   * @param results
   * @param fd
   * @return
   */
  public static List<ASTFeatureConfiguration> transformResultToFC(String resultName,
      List<Map<String, Integer>> results, ASTFeatureDiagram fd) {
    if (null == results) {
      return null;
    }
    List<ASTFeatureConfiguration> resultList = new ArrayList<>();
    for (int i = 0; i < results.size(); i++) {
      Map<String, Integer> result = results.get(i);
      if (null == result) {
        return null;
      }
      resultList.add(transformResultToFC(resultName + i, result, fd));
    }
    return resultList;
  }
}
