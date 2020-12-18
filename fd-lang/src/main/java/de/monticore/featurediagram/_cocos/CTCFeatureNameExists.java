/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Checks, whether the feature names used whithin cross-tree constraints refer to actual features
 * of a feature model.
 */
public class CTCFeatureNameExists
    implements FeatureDiagramASTFeatureDiagramCoCo, ExpressionsBasisVisitor2 {

  protected Map<String, SourcePosition> ctcnames;

  protected FeatureDiagramTraverser traverser;

  public CTCFeatureNameExists(){
    ctcnames = new HashMap<>();
    traverser = FeatureDiagramMill.traverser();
    traverser.add4ExpressionsBasis(this);
  }

  @Override
  public void check(ASTFeatureDiagram node) {
    node.accept(traverser);
    ctcnames.forEach((name, pos) -> {
      if (!node.getSpannedScope().resolveFeature(name).isPresent()) {
        Log.error("0xFD006 A cross-tree constraint refers to the feature '" + name
            + "' that is not available in the current feature model.", pos);
      }
    });
  }

  @Override
  public void visit(ASTNameExpression node) {
    ctcnames.put(node.getName(), node.get_SourcePositionStart());
  }
}
