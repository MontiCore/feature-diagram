/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureConstraint;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralFilter extends Analysis<Set<ASTFeatureConfiguration>> {

  public GeneralFilter(List<ASTExpression> filters) {
    super();
    filters.forEach(filter -> {
      ASTFeatureConstraint fd = FeatureDiagramMill.featureConstraintBuilder().setConstraint(filter)
          .build();
      super.getFeatureModel().addFDElements(fd);
    });
  }

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    setResult(new HashSet<>(configurations));
  }
}
