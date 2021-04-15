/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This analysis returns all features of a passed FD that are not contained in any valid
 * configuration of FD.
 * !! WARNING: This analysis can be slow for large FDs !!
 */
public class DeadFeature {

  public List<String> perform(ASTFeatureDiagram fd) {
    //Step 1: initialize a list with all features of the passed FD
    List<String> allFeatures = new ArrayList<>(fd.getAllFeatures());

    // Step 2: populate a map that maps feature names to the number of times these occur
    // in all valid configurations of fd
    Map<String, Integer> occurrences = FDAnalyses.countOccurrencesInFCs(fd);

    // Step 3: From the list of all features, remove all features occuring at least in one valid FC
    allFeatures.removeAll(occurrences.keySet());
    return allFeatures;
  }

}
