/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._ast.ASTGroupPart;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor2;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all optional features of a feature model as a List<String> result
 */
public class OptionalFeatureCollector implements FeatureDiagramVisitor2 {

  public static List<String> getOptionalFeatures(ASTFeatureDiagram fd){
    FeatureDiagramTraverser traverser  = FeatureDiagramMill.traverser();
    OptionalFeatureCollector visitor = new OptionalFeatureCollector();
    traverser.add4FeatureDiagram(visitor);
    fd.accept(traverser);
    return visitor.getOptionalFeatures();
  }

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
