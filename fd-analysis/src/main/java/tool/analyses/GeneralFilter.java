/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import featureconfiguration._ast.ASTFeatureConfiguration;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTFeatureConstraint;

import java.util.*;

public class GeneralFilter extends Analysis<Set<ASTFeatureConfiguration>> {

  public GeneralFilter(List<ASTExpression> filters) {
    super();
    filters.forEach(filter ->{
      ASTFeatureConstraint fd = FeatureDiagramMill.featureConstraintBuilder().setConstraint(filter).build();
      super.getFeatureModel().getAstNode().addFDElement(fd);
            });
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(new HashSet<>(configurations));
  }
}
