package de.monticore.fd.conformance;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import de.monticore.fd.conformance.fdmapping.FDMCMapping;
import de.monticore.fd.conformance.fdmapping.FDNameMapping;
import de.monticore.fd.conformance.fdmapping._ast.ASTFDMapping;
import de.monticore.fd.conformance.loader.FDLoader;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class FDConformanceChecker {
  public static boolean checkConformance(
      ASTFDCompilationUnit refFD, ASTFDCompilationUnit conFD, ASTFDMapping mapping) {
    return checkConformance(
        refFD, conFD, mapping, Set.of(ConfParams.MC_MAPPING, ConfParams.NAME_MAPPING));
  }

  public static boolean checkConformance(
      ASTFDCompilationUnit refFD,
      ASTFDCompilationUnit conFD,
      ASTFDMapping mapping,
      Set<ConfParams> params) {
    Context ctx = FDLoader.buildContext();

    // convert reference anc concrete automaton and to smt
    SMTFDiagram ref = new SMTFDiagram(refFD, ctx, s -> s + "_ref");
    SMTFDiagram con = new SMTFDiagram(conFD, ctx, s -> s + "_con");
    BoolExpr[] map = mapping2smt(ref, con, mapping, ctx, params);

    Solver solver = ctx.mkSolver();

    // forbid all configuration of the reference model
    solver.add(ctx.mkNot(ref.getFDConstraint()));

    // allow all configurations of the concrete model
    solver.add(con.getFDConstraint());

    // add mapping constraints
    solver.add(map);

    // force the concrete model not to be empty ( the root feature must exist)
    solver.add(con.getFeature(conFD.getFeatureDiagram().getRootFeature()));

    if (solver.check().equals(Status.UNSATISFIABLE)) {
      Log.println("===== CONFORM =====");
      return true;
    } else {
      Log.println("===== NOT CONFORM =====");
      Model model = solver.getModel();
      Log.println("Concrete Configuration: " + computeWitness(con, model)  + "is valid.");
      Log.println("Reference Configuration: " + computeWitness(ref, model) + " is NOT allowed!");

      return false;
    }
  }

  private static BoolExpr[] mapping2smt(
      SMTFDiagram ref,
      SMTFDiagram con,
      ASTFDMapping fdMapping,
      Context ctx,
      Set<ConfParams> params) {
    List<BoolExpr> mappingConstraints = new ArrayList<>();
    List<String> nonMappedFeatures;

    if (params.contains(ConfParams.MC_MAPPING)) {
      FDMCMapping FDMCMapping = new FDMCMapping(fdMapping);
      mappingConstraints.addAll(FDMCMapping.map(ref, con, ctx));

      // filter concrete features not present in the mc mapping
      nonMappedFeatures =
          con.getFeatureDiagram().getFeatureDiagram().getAllFeatures().stream()
              .filter(f -> !fdMapping.getConcreteFeatures().contains(f))
              .collect(Collectors.toList());
    } else {
      nonMappedFeatures = con.getFeatureDiagram().getFeatureDiagram().getAllFeatures();
    }

    if (params.contains(ConfParams.NAME_MAPPING)) {
      FDNameMapping nameMapping = new FDNameMapping(nonMappedFeatures);
      mappingConstraints.addAll(nameMapping.map(ref, con, ctx));
    }

    return mappingConstraints.toArray(new BoolExpr[0]);
  }

  private static Set<String> computeWitness(SMTFDiagram smtFDiagram, Model model) {

    Set<String> res = new HashSet<>();
    for (Map.Entry<String, BoolExpr> entry : smtFDiagram.getAllFeatures().entrySet()) {
      BoolExpr expr = (BoolExpr) model.eval(entry.getValue(), true);
      if (expr.getBoolValue().equals(Z3_lbool.Z3_L_TRUE)) {
        res.add(entry.getKey());
      }
    }
    return res;
  }
}
