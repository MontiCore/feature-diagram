package de.monticore.fd.conformance;

import com.microsoft.z3.*;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import de.monticore.fd.conformance.loader.FDLoader;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SMTFDiagramTest extends FDAbstractTest {
  private SMTFDiagram smtFDiagram;

  private final Context ctx = buildContext();

  private Solver solver;

  @BeforeEach
  @Disabled
  public void setup() {
    Log.init();
    FeatureDiagramMill.init();
    FeatureDiagramMill.globalScope();

    // load FD model and convert to smt
    ASTFDCompilationUnit fd = FDLoader.loadAndCheckFD(RELATIVE_MODEL_PATH + "fd2smt/Smtfd.fd");
    smtFDiagram = new SMTFDiagram(fd, ctx, string -> string);

    // add constraints ton the solver
    solver = ctx.mkSolver();
    solver.add(smtFDiagram.getFeature("A"));
    solver.add(smtFDiagram.getFDConstraint());

    // solve
    Assertions.assertSame(solver.check(), Status.SATISFIABLE);
  }

  @Test
  public void testAndGroup() {
    Set<String> config = getFeatureConfiguration(smtFDiagram, solver.getModel());
    boolean res = config.contains("B") && config.contains("C") && config.contains("D");
    Assertions.assertTrue(res);
  }

  @Test
  public void testOrGroup() {
    Set<String> config = getFeatureConfiguration(smtFDiagram, solver.getModel());
    boolean res = config.contains("E") || config.contains("F") || config.contains("G");
    Assertions.assertTrue(res);
  }

  @Test
  public void testXOrGroup() {
    Set<String> config = getFeatureConfiguration(smtFDiagram, solver.getModel());
    boolean res = config.contains("H") ^ config.contains("I") ^ config.contains("J");
    Assertions.assertTrue(res);
    System.out.println(solver.getModel());
  }

  @Test
  public void testRequiresAndExcludes() {
    Set<String> config = getFeatureConfiguration(smtFDiagram, solver.getModel());
    System.out.println(config);
    boolean res = config.contains("K") ^ config.contains("L");
    Assertions.assertTrue(res);
  }
}
