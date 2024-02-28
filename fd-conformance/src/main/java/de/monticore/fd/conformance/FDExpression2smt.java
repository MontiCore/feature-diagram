/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprFactory;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeFactory;
import de.monticore.ocl2smt.ocl2smt.oclExpr2smt.OCLExprConverter;
import de.se_rwth.commons.logging.Log;
import java.util.Map;

public class FDExpression2smt extends OCLExprConverter<Z3ExprAdapter> {
  public final Map<String, BoolExpr> constants;
  protected Context ctx;

  public FDExpression2smt(
      Z3ExprFactory eFactory,
      Z3TypeFactory tFactory,
      Context ctx,
      Map<String, BoolExpr> constants) {
    super(eFactory, tFactory);
    this.constants = constants;
    this.ctx = ctx;
  }

  @Override
  protected Z3ExprAdapter convert(ASTNameExpression node) {
    return new Z3ExprAdapter(constants.get(node.getName()), (Z3TypeAdapter) tFactory.mkBoolType());
  }

  @Override
  public Z3ExprAdapter convertExpr(ASTExpression node) {
    Z3ExprAdapter res;
    if (node instanceof ASTRequires) {
      res = convert((ASTRequires) node);
    } else if (node instanceof ASTExcludes) {
      res = convert((ASTExcludes) node);
    } else if (node instanceof ASTNameExpression) {
      res = (convert((ASTNameExpression) node));
    } else {
      res = super.convertExpr(node);
    }
    return res;
  }

  protected Z3ExprAdapter convert(ASTRequires requires) {
    return eFactory.mkImplies(convertExpr(requires.getLeft()), convertExpr(requires.getRight()));
  }

  protected Z3ExprAdapter convert(ASTExcludes excludes) {
    BoolExpr left = (BoolExpr) convertExpr(excludes.getLeft()).getExpr();
    BoolExpr right = (BoolExpr) convertExpr(excludes.getRight()).getExpr();
    return new Z3ExprAdapter(ctx.mkXor(left, right), (Z3TypeAdapter) tFactory.mkBoolType());
  }

  @Override
  protected Z3ExprAdapter convert(ASTFieldAccessExpression node) {
    Log.error("FieldAccessExpression not converted for FD-Expressions");
    return null;
  }

  public BoolExpr convertBoolExpr(ASTExpression constraint) {
    return (BoolExpr) convertExpr(constraint).getExpr();
  }
}
