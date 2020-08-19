/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;
import mcfdtool.visitors.OptionalFeatureCollector;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This analysis returns all features that are explicitly marked optional in the passed FD, although
 * these are part of every valid configuration of FD.
 * !! WARNING: This analysis can be slow for large FDs !!
 */
public class FalseOptional {

  /**
   * Calculates the set of false optional features. False optional features are those that are
   * in the intersection of (1) the set of all features marked as optional and (2) the set of
   * features that occur in all valid configurations
   *
   * @param fd
   * @return
   */
  public List<String> perform(ASTFeatureDiagram fd) {
    List<String> optionalFeatures = getOptionalFeatures(fd);
    Set<String> alwaysSelected = getFeaturesAlwaysSelected(fd);
    optionalFeatures.retainAll(alwaysSelected);
    return optionalFeatures;
  }

  /**
   * calculate a list of features directly marked as optional in the passed FD
   *
   * @param fd
   * @return
   */
  protected List<String> getOptionalFeatures(ASTFeatureDiagram fd) {
    OptionalFeatureCollector finder = new OptionalFeatureCollector();
    fd.accept(finder);
    return finder.getOptionalFeatures();
  }

  /**
   * calculate a set of features that are contained in all valid configurations of the passes FD
   *
   * @param fd
   * @return
   */
  protected Set<String> getFeaturesAlwaysSelected(ASTFeatureDiagram fd) {
    // Step 1: populate a map that maps feature names to the number of times these occur
    // in all valid configurations of fd
    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd).build();
    List<Map<String, Integer>> allSolutions = Solvers.getSolver().getAllSolutions(model);
    List<ASTFeatureConfiguration> allConfigurations = Solvers
        .transformResultToFC("IntermediateResult", allSolutions, fd);
    Map<String, Integer> occurrences = FDAnalyses.countOccurrencesInFCs(allConfigurations);

    //Step 2: populate a set with features that are selected in every valid configuration
    Set<String> alwaysSelected = new HashSet<>();
    int numberOfValidFCs = allConfigurations.size();
    for (String feature : occurrences.keySet()) {
      //if the feature occurs in every valid configuration
      if (numberOfValidFCs == occurrences.get(feature)) {
        alwaysSelected.add(feature);
      }
    }

    return alwaysSelected;
  }

}
