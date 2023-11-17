package de.monticore.fd.conformance;

import org.junit.jupiter.api.Test;

public class FDConformanceToolTest extends FDAbstractTest {

  @Test
  public void toolTest() {
    String refModel = RELATIVE_MODEL_PATH + "conf/reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "conf/concrete.fd";
    String mapping = RELATIVE_MODEL_PATH + "conf/mapping.map";
    new FDConformanceTool().run(new String[] {"-c", conModel, "-r", refModel, "-m", mapping});
  }

  @Test
  public void evaluationCoolCarInvalidTest() {
    String refModel = RELATIVE_MODEL_PATH + "eval/coolcar/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/coolcar/Concrete.fd";
    String mapping = RELATIVE_MODEL_PATH + "eval/coolcar/MapV1.map";
    new FDConformanceTool().run(new String[] {"-c", conModel, "-r", refModel, "-m", mapping});
  }

  @Test
  public void evaluationCoolCarValidTest() {
    String refModel = RELATIVE_MODEL_PATH + "eval/coolcar/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/coolcar/Concrete.fd";
    String mapping = RELATIVE_MODEL_PATH + "eval/coolcar/MapV2.map";
    new FDConformanceTool().run(new String[] {"-c", conModel, "-r", refModel, "-m", mapping});
  }

  @Test
  public void evaluationVehicleTest() {
    String refModel = RELATIVE_MODEL_PATH + "eval/vehicle/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/vehicle/Concrete.fd";
    String mapping = RELATIVE_MODEL_PATH + "eval/vehicle/mapping.map";
    new FDConformanceTool().run(new String[] {"-c", conModel, "-r", refModel, "-m", mapping});
  }

  @Test
  public void evaluationMachineTest() {
    String refModel = RELATIVE_MODEL_PATH + "eval/machine/Reference.fd";
    String conModel = RELATIVE_MODEL_PATH + "eval/machine/Concrete.fd";
    String mapping = RELATIVE_MODEL_PATH + "eval/machine/mapping.map";
    new FDConformanceTool().run(new String[] {"-c", conModel, "-r", refModel, "-m", mapping});
  }
}
