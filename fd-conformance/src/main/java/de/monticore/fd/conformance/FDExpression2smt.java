package de.monticore.fd.conformance;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.ocl2smt.ocl2smt.expressionconverter.Expression2smt;
import de.se_rwth.commons.logging.Log;
import java.util.Map;
import java.util.Optional;

public class FDExpression2smt extends Expression2smt {
  public final Map<String, BoolExpr> constants;

  public FDExpression2smt(Context ctx, Map<String, BoolExpr> constants) {
    this.constants = constants;
    this.ctx = ctx;
  }

  @Override
  protected Expr<? extends Sort> convert(ASTNameExpression node) {
    return constants.get(node.getName());
  }

  @Override
  protected Optional<BoolExpr> convertBoolExprOpt(ASTExpression node) {
    Optional<BoolExpr> res = super.convertBoolExprOpt(node);
    if (res.isEmpty()) {
      if (node instanceof ASTRequires) {
        res = Optional.ofNullable(convert((ASTRequires) node));
      } else if (node instanceof ASTExcludes) {
        res = Optional.ofNullable(convert((ASTExcludes) node));
      } else if (node instanceof ASTNameExpression) {
        res = Optional.ofNullable((BoolExpr) convert((ASTNameExpression) node));
      }
    }
    return res;
  }

  protected BoolExpr convert(ASTRequires requires) {
    return ctx.mkImplies(convertBoolExpr(requires.getLeft()), convertBoolExpr(requires.getRight()));
  }

  protected BoolExpr convert(ASTExcludes excludes) {
    return ctx.mkXor((convertBoolExpr(excludes.getLeft())), convertBoolExpr(excludes.getRight()));
  }

  @Override
  protected Expr<? extends Sort> convert(ASTFieldAccessExpression node) {
    Log.error("FieldAccessExpression not converted for FD-Expressions");
    return null;
  }
}
