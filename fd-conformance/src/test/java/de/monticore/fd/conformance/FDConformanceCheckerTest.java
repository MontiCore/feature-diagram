package de.monticore.fd.conformance;

import de.monticore.fd.conformance.fdmapping._ast.ASTFDMapping;
import de.monticore.fd.conformance.loader.FDLoader;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FDConformanceCheckerTest extends FDAbstractTest {

  ASTFDCompilationUnit ref;
  ASTFDCompilationUnit con;
  ASTFDMapping mapping;

  public void setup(String refPath, String conPath, String mappingPath) {
    ref = FDLoader.loadAndCheckFD(refPath);
    con = FDLoader.loadAndCheckFD(conPath);
    if (mappingPath != null) {
      mapping = FDLoader.loadAndCheckMapping(refPath, conPath, mappingPath);
    }
  }

  @Test
  public void testCoolCarInvalid() {
    String refModel = RELATIVE_MODEL_PATH + "eval/coolcar/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/coolcar/Concrete.fd";
    String map = RELATIVE_MODEL_PATH + "eval/coolcar/MapV1.fdmap";
    setup(refModel, conModel, map);

    // check conformance
    Assertions.assertFalse(FDConformanceChecker.checkConformance(ref, con, mapping));
  }

  @Test
  public void testCoolCarValid() {
    String refModel = RELATIVE_MODEL_PATH + "eval/coolcar/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/coolcar/Concrete.fd";
    String map = RELATIVE_MODEL_PATH + "eval/coolcar/MapV2.fdmap";
    setup(refModel, conModel, map);

    // check conformance
    Assertions.assertTrue(FDConformanceChecker.checkConformance(ref, con, mapping));
  }

  @Test
  public void conformanceCheckerTest() {
    String refModel = RELATIVE_MODEL_PATH + "conf/reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "conf/concrete.fd";
    String map = RELATIVE_MODEL_PATH + "conf/mapping.fdmap";
    setup(refModel, conModel, map);

    // check conformance
    Assertions.assertFalse(FDConformanceChecker.checkConformance(ref, con, mapping));
  }

  @Test
  public void conformanceCheckerWithNameMapping() {
    String refModel = RELATIVE_MODEL_PATH + "conf/reference.fd";
    setup(refModel, refModel, null);

    // check conformance
    Assertions.assertTrue(
        FDConformanceChecker.checkConformance(ref, con, mapping, Set.of(ConfParams.NAME_MAPPING)));
  }
}
