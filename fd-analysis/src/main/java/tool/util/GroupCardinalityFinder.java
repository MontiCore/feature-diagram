package tool.util;

import featurediagram._ast.ASTOrGroup;
import featurediagram._ast.ASTXorGroup;
import featurediagram._visitor.FeatureDiagramVisitor;

public class GroupCardinalityFinder implements FeatureDiagramVisitor {

  String min;
  String max;

  public String getMin() {
    return min;
  }

  public String getMax() {
    return max;
  }

  @Override
  public void visit(ASTOrGroup astOrGroup) {
    min = "1";
    max = ""+astOrGroup.getFeatureList().size();
  }

  @Override
  public void visit(ASTXorGroup astXorGroup) {
    min ="1";
    max="1";
  }
}
