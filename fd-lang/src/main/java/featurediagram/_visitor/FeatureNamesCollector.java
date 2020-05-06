package featurediagram._visitor;

import featurediagram._ast.ASTFeatureTreeRule;

import java.util.HashMap;

public class FeatureNamesCollector implements FeatureDiagramVisitor{

  public enum Occurrence {LEFT, RIGHT, BOTH}

  private HashMap<String, Occurrence> names = new HashMap<>();
  @Override
  public void visit(ASTFeatureTreeRule node) {
    String name = node.getName();
    if(names.getOrDefault(name, Occurrence.LEFT) != Occurrence.LEFT){
      names.put(name, Occurrence.BOTH);
    }else {
      names.put(name, Occurrence.LEFT);
    }

    node.getFeatureGroup().getNameList().stream()
            .forEach(rightName -> {
              if(names.getOrDefault(name, Occurrence.RIGHT) != Occurrence.RIGHT){
                names.put(name, Occurrence.BOTH);
              }else {
                names.put(name, Occurrence.RIGHT);
              }
            });
  }

  public HashMap<String, Occurrence> getNames() {
    return names;
  }
}
