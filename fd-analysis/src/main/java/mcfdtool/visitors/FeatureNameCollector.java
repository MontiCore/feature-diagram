/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featurediagram._ast.ASTFeature;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor2;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all Featurenames in a List<String> result.
 * (unsorted)
 */
public class FeatureNameCollector implements FeatureDiagramVisitor2 {

  List<String> names = new ArrayList<>();

  @Override
  public void visit(ASTFeature ast) {
    names.add(ast.getName());
  }

  public List<String> getNames() {
    return names;
  }
}
