/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class CTCFeatureNameExists
    implements FeatureDiagramASTFeatureDiagramCoCo, FeatureDiagramVisitor {

  private Map<String, SourcePosition> ctcnames = new HashMap<>();

  @Override
  public void check(ASTFeatureDiagram node) {
    node.accept(this);
    ctcnames.forEach((name, pos) -> {
      if (!node.getSpannedScope().resolveFeature(name).isPresent()) {
        Log.error("0xFD006 The cross-tree constraint on " + name
            + " must operate on features that are available in the current feature model.", pos);
      }
    });
  }

  @Override
  public void visit(ASTNameExpression node) {
    ctcnames.put(node.getName(), node.get_SourcePositionStart());
  }
}
