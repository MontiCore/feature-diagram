/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._visitor;

import de.monticore.featurediagram._ast.ASTFeatureTreeRule;
import de.monticore.featurediagram._ast.ASTGroupPart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This visitor traverses the AST of a feature model and collects all feature names.
 * One the same time, it collects for each feature name whether it occurs only on the left-hand side
 * of feature tree rules, only on the right-hand side, or both. If a feature name occurs
 *  - only on the left-hand side:  it is a candidate for a root feature.
 *  - only on the right-hand side: it is a leaf feature.
 *  - both: it is an inner feature in the feature tree
 */
public class FeatureNamesCollector implements FeatureDiagramVisitor {

  private HashMap<String, Occurrence> occurrences = new HashMap<>();

  @Override
  public void visit(ASTFeatureTreeRule node) {
    String name = node.getName();
    if (occurrences.getOrDefault(name, Occurrence.LEFT) != Occurrence.LEFT) {
      occurrences.put(name, Occurrence.BOTH);
    }
    else {
      occurrences.put(name, Occurrence.LEFT);
    }

    node.getFeatureGroup().streamGroupParts().map(ASTGroupPart::getName)
        .forEach(rightName -> {
          if (occurrences.getOrDefault(rightName, Occurrence.RIGHT) != Occurrence.RIGHT) {
            occurrences.put(rightName, Occurrence.BOTH);
          }
          else {
            occurrences.put(rightName, Occurrence.RIGHT);
          }
        });
  }

  public HashMap<String, Occurrence> getOccurrences() {
    return occurrences;
  }

  public List<String> getOccurrences(Occurrence o) {
    return getOccurrences().entrySet().stream()
        .filter(e -> o == e.getValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }
}
