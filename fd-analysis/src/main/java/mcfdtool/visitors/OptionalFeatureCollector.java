/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featurediagram._ast.ASTGroupPart;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all optional features of a feature model as a List<String> result
 */
public class OptionalFeatureCollector implements FeatureDiagramVisitor {

  private List<String> optionalFeatures = new ArrayList<>();

  @Override
  public void visit(ASTGroupPart part) {
    if (part.isOptional()) {
      optionalFeatures.add(part.getName());
    }
  }

  public List<String> getOptionalFeatures() {
    return optionalFeatures;
  }
}
