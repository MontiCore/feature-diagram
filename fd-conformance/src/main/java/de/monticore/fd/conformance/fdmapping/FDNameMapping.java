package de.monticore.fd.conformance.fdmapping;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FDNameMapping implements IFDMapping {
  List<String> features;

  public FDNameMapping(List<String> features) {
    this.features = features;
  }

  @Override
  public List<BoolExpr> map(SMTFDiagram ref, SMTFDiagram con, Context ctx) {

    List<BoolExpr> res = new ArrayList<>();

    for (String conFeature : features) {
      Optional<BoolExpr> refFeature = Optional.ofNullable(ref.getFeature(conFeature));
      refFeature.ifPresent(
          boolExpr -> res.add(ctx.mkImplies(con.getFeature(conFeature), boolExpr)));
    }
    return res;
  }
}
