/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance.fdmapping;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import de.monticore.fd.conformance.fdmapping._ast.ASTFDMapping;
import de.monticore.fd.conformance.fdmapping._ast.ASTFDMappingRule;
import java.util.ArrayList;
import java.util.List;

public class FDMCMapping implements IFDMapping {
  protected ASTFDMapping mapping;

  public FDMCMapping(ASTFDMapping mapping) {
    this.mapping = mapping;
  }

  @Override
  public List<BoolExpr> map(SMTFDiagram ref, SMTFDiagram con, Context ctx) {

    List<BoolExpr> res = new ArrayList<>();

    for (ASTFDMappingRule rule : mapping.getFDMappingRuleList()) {

      BoolExpr left =
          ctx.mkAnd(rule.getLeftFeatures().stream().map(con::getFeature).toArray(BoolExpr[]::new));
      BoolExpr right = ref.getFeature(rule.getRightSide().getName());
      res.add(ctx.mkImplies(left, right));
    }
    return res;
  }
}
