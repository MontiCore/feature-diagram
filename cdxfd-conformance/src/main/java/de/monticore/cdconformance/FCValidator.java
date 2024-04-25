/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FCValidator {

  /****
   * Check if a feature configuration (fc) is in the semantic of a feature diagram. The following conditions must hold:
   * 1. The feature configuration must contain the root feature.
   * 2. All features in the feature configuration must appear in the feature diagram.
   * 3. The feature configuration must align with the fd-contains and tree-rules.
   *
   * @param fDiagram the feature diagram.
   * @param fConfiguration the feature configuration
   */
  public static boolean checkFcValidity(
      ASTFDCompilationUnit fDiagram, ASTFCCompilationUnit fConfiguration) {
    String logger = CDxFDConformance.class.getName();

    List<String> diagramFeatures = fDiagram.getFeatureDiagram().getAllFeatures();
    List<String> configFeatures = CDxFDConformanceUtil.collectFeaturesNames(fConfiguration);

    // check if all features in the configuration are in the diagram
    if (!new HashSet<>(diagramFeatures).containsAll(configFeatures)) {
      configFeatures.removeAll(diagramFeatures);
      Log.info(
          "The following feature does not appear in the Feature Diagram: " + configFeatures,
          logger);
      return false;
    }

    // check if the configuration contains the root feature
    String root = fDiagram.getFeatureDiagram().getRootFeature();
    if (!configFeatures.contains(root)) {
      Log.info("The configuration must contains the root Feature: " + root, logger);
      return false;
    }

    Context ctx = new Context();
    SMTFDiagram smtFd = new SMTFDiagram(fDiagram, ctx, x -> x);
    Solver solver = ctx.mkSolver();

    for (int i = 0; i < diagramFeatures.size(); i++) {
      BoolExpr assertion;
      if (configFeatures.contains(diagramFeatures.get(i))) {
        assertion = smtFd.getFeature(diagramFeatures.get(i));
      } else {
        assertion = ctx.mkNot(smtFd.getFeature(diagramFeatures.get(i)));
      }

      BoolExpr tracker = ctx.mkBoolConst(String.valueOf(i));
      solver.assertAndTrack(assertion, tracker);
    }

    solver.add(smtFd.getFDConstraint());
    Status status = solver.check();

    if (status == Status.UNKNOWN) {
      Log.info("Unable to check Validity.", CDxFDConformance.class.getName());
      return false;
    } else if (status == Status.UNSATISFIABLE) {
      Log.info("The given configuration is not Valid for the Feature Diagram", logger);
      Log.info(debugSolverConclusion(solver, diagramFeatures, root), logger);
      return false;
    } else {
      Log.info("The given configuration is Valid for the Feature Diagram", logger);
      return true;
    }
  }

  private static String debugSolverConclusion(Solver solver, List<String> features, String root) {
    List<String> conflictingFeatures = new ArrayList<>();

    for (BoolExpr tracker : solver.getUnsatCore()) {
      String trackId = tracker.toString().replaceAll("\\|", "");
      conflictingFeatures.add(features.get(Integer.parseInt(trackId)));
    }

    if (conflictingFeatures.isEmpty()
        || conflictingFeatures.size() == 1 && conflictingFeatures.get(0).equals(root)) {
      return "The Feature Diagram is Inconsistent";
    } else {
      return "There is a problem with the incarnation of the following features in "
          + "the concrete class diagram: "
          + conflictingFeatures
          + ", either some of those features "
          + "need to be incarnated or some shouldn't be incarnated.";
    }
  }
}
