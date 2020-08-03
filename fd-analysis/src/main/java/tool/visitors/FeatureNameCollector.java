/* (c) https://github.com/MontiCore/monticore */
package tool.visitors;

import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all Featurenames in List<String> result.
 * (unsorted)
 */

public class FeatureNameCollector implements FeatureDiagramVisitor {

  List<String> names = new ArrayList<>();

  @Override
  public void visit(FeatureSymbol symbol) {
    names.add(symbol.getName());
  }

  public List<String> getNames() {
    return names;
  }
}
