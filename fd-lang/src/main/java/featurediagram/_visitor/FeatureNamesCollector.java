/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._ast.ASTGroupPart;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureNamesCollector implements FeatureDiagramVisitor {

  public enum Occurrence {LEFT, RIGHT, BOTH}

  private HashMap<String, Occurrence> occurrences = new HashMap<>();
  @Override
  public void visit(ASTFeatureTreeRule node) {
    String name = node.getName();
    if(occurrences.getOrDefault(name, Occurrence.LEFT) != Occurrence.LEFT){
      occurrences.put(name, Occurrence.BOTH);
    }else {
      occurrences.put(name, Occurrence.LEFT);
    }

    node.getFeatureGroup().streamGroupParts().map(ASTGroupPart::getName)
            .forEach(rightName -> {
              if(occurrences.getOrDefault(rightName, Occurrence.RIGHT) != Occurrence.RIGHT){
                occurrences.put(rightName, Occurrence.BOTH);
              }else {
                occurrences.put(rightName, Occurrence.RIGHT);
              }
            });
  }

  public HashMap<String, Occurrence> getOccurrences() {
    return occurrences;
  }

  public List<String> getOccurrences(Occurrence o) {
    return  getOccurrences().entrySet().stream()
        .filter(e -> o == e.getValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }
}
