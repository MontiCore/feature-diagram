/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._ast.ASTFeatureTreeRule;

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

    node.getFeatureGroup().getGroupPartList().stream()
            .forEach(rightName -> {
              if(occurrences.getOrDefault(rightName, Occurrence.RIGHT) != Occurrence.RIGHT){
                occurrences.put(rightName.getName(), Occurrence.BOTH);
              }else {
                occurrences.put(rightName.getName(), Occurrence.RIGHT);
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
