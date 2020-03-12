package tool.analyses;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import tool.transform.trafos.ComplexConstraint2FZN;

import java.util.*;

public class GeneralFilter extends Analysis<Set<Map<String, Boolean>>> {

  public GeneralFilter(List<ASTExpression> filters) {
    super();
    super.getModelBuilder().addFeatureModelFZNTrafo(new ComplexConstraint2FZN(filters));
  }

  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(new HashSet<>(configurations));
  }
}
