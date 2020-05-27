/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTConstraint;
import featurediagram._ast.ASTFeatureConstraint;

import java.util.*;

public class GeneralFilter extends Analysis<Set<Map<String, Boolean>>> {

  public GeneralFilter(List<ASTConstraint> filters) {
    super();
    filters.forEach(filter ->{
      ASTFeatureConstraint fd = FeatureDiagramMill.featureConstraintBuilder().setConstraintExpression(filter).build();
      super.getFeatureModel().getAstNode().addFDElement(fd);
            });
  }

  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(new HashSet<>(configurations));
  }
}
