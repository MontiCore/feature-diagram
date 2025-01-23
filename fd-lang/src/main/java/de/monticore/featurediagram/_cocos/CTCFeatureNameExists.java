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
public class CTCFeatureNameExists implements FeatureDiagramASTFeatureDiagramCoCo {

  @Override
  public void check(ASTFeatureDiagram node) {
    Checker c = new Checker(node);
    c.getCTCNames().forEach((name, pos) -> {
      if (!node.getSpannedScope().resolveFeature(name).isPresent()) {
        Log.error("0xFD006 A cross-tree constraint refers to the feature '" + name
            + "' that is not available in the current feature model.", pos);
      }
    });
  }

  class Checker implements ExpressionsBasisVisitor2 {

    protected Map<String, SourcePosition> ctcnames;

    protected FeatureDiagramTraverser traverser;

    public Checker(ASTFeatureDiagram node) {
      ctcnames = new HashMap<>();
      traverser = FeatureDiagramMill.inheritanceTraverser();
      traverser.add4ExpressionsBasis(this);
      node.accept(traverser);
    }

    @Override
    public void visit(ASTNameExpression node) {
      ctcnames.put(node.getName(), node.get_SourcePositionStart());
    }

    public Map<String, SourcePosition> getCTCNames() {
      return ctcnames;
    }
  }
}
