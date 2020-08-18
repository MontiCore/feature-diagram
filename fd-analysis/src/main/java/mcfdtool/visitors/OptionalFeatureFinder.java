/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featurediagram._ast.ASTGroupPart;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;

import java.util.ArrayList;
import java.util.List;

// TODO: comment, explain class

public class OptionalFeatureFinder implements FeatureDiagramVisitor {
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