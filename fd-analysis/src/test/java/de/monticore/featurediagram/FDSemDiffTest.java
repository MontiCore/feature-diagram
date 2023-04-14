/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import com.google.common.collect.Lists;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._visitor.UnSelectedFeatureCollector;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import fddiff.FDSemDiff;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

public class FDSemDiffTest {

  private static final String BASE_PATH = Paths.get("src", "test", "resources", "fddiff")
      .toString();

  private FeatureDiagramParser parser;

  private FDSemDiff semDiff;

  public static void assertSelectionEquals(List<String> actual, String... expected) {
    assertEquals(actual.size(), expected.length);
    assertTrue(actual.containsAll(Lists.newArrayList(expected)));
  }

  @Before
  public void setup() {
    parser = new FeatureDiagramParser();
    semDiff = new FDSemDiff();
  }

  @Test
  public void testSemDiff_car_car() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("car"), getFD("car")).isPresent());
    assertFalse(semDiff.semDiffClosedWorld(getFD("car"), getFD("car")).isPresent());
  }

  @Test
  public void testSemDiff_car_car1_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("car"), getFD("car1"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "engine", "car", "hybrid");
  }

  @Test
  public void testSemDiff_car_car2_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("car"), getFD("car2")).isPresent());
  }

  @Test
  public void testSemDiff_car1_car_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("car1"), getFD("car"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "engine", "car", "electric", "gas");
  }

  @Test
  public void testSemDiff_car1_car1_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("car1"), getFD("car1")).isPresent());
  }

  @Test
  public void testSemDiff_car1_car2_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("car1"), getFD("car2"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "engine", "car", "gas", "electric");
  }

  @Test
  public void testSemDiff_car2_car_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("car2"), getFD("car")).isPresent());
  }

  @Test
  public void testSemDiff_car2_car1_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("car2"), getFD("car1"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "hybrid", "engine", "car");
  }

  @Test
  public void testSemDiff_car2_car2_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("car2"), getFD("car2")).isPresent());
  }

  @Test
  public void testSemDiff_tablet1_tablet1_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("tablet1"), getFD("tablet1")).isPresent());
  }

  @Test
  public void testSemDiff_tablet1_tablet2_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet1"), getFD("tablet2"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "display", "P100", "dis11", "processor",
        "m64GB", "dis12");
  }

  @Test
  public void testSemDiff_tablet1_tablet3_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet1"), getFD("tablet3"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "P100", "m64GB", "display", "dis11", "dis12",
        "processor");
  }

  @Test
  public void testSemDiff_tablet2_tablet1_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet2"), getFD("tablet1"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "m64GB", "display", "P100", "processor",
        "dis12");
  }

  @Test
  public void testSemDiff_tablet2_tablet2_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("tablet2"), getFD("tablet2")).isPresent());
  }

  @Test
  public void testSemDiff_tablet2_tablet3_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet2"), getFD("tablet3"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "m64GB", "display", "P200", "processor",
        "dis11");
  }

  @Test
  public void testSemDiff_tablet3_tablet1_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet3"), getFD("tablet1"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "m128GB", "display", "P100", "processor",
        "dis12");
  }

  @Test
  public void testSemDiff_tablet3_tablet2_open_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffOpenWorld(getFD("tablet3"), getFD("tablet2"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "tablet", "memory", "m256GB", "display", "P100", "processor",
        "dis11");
  }

  @Test
  public void testSemDiff_tablet3_tablet3_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("tablet3"), getFD("tablet3")).isPresent());
  }

  @Test
  public void testSemDiff_carLocking_carPhone_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("carLocking"), getFD("carPhone")).isPresent());
  }

  @Test
  public void testSemDiff_carLocking_carLockingEngine_open_world() {
    assertFalse(
        semDiff.semDiffOpenWorld(getFD("carLocking"), getFD("carLockingEngine")).isPresent());
  }

  @Test
  public void testSemDiff_carPhone_carLocking_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("carPhone"), getFD("carLocking")).isPresent());
  }

  @Test
  public void testSemDiff_carPhone_carLockingEngine_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("carPhone"), getFD("carLockingEngine")).isPresent());
  }

  @Test
  public void testSemDiff_carLockingEngine_carLocking_open_world() {
    assertFalse(
        semDiff.semDiffOpenWorld(getFD("carLockingEngine"), getFD("carLocking")).isPresent());
  }

  @Test
  public void testSemDiff_carLockingEngine_carPhone_open_world() {
    assertFalse(semDiff.semDiffOpenWorld(getFD("carLockingEngine"), getFD("carPhone")).isPresent());
  }

  @Test
  public void testSemDiff_carLocking_carPhone_closed_world() {
    assertFalse(semDiff.semDiffClosedWorld(getFD("carLocking"), getFD("carPhone")).isPresent());
  }

  @Test
  public void testSemDiff_carLocking_carLockingEngine_closed_world() {
    assertFalse(
        semDiff.semDiffClosedWorld(getFD("carLocking"), getFD("carLockingEngine")).isPresent());
  }

  @Test
  public void testSemDiff_carPhone_carLocking_closed_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffClosedWorld(getFD("carPhone"), getFD("carLocking"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "car", "locking", "phone");
  }

  @Test
  public void testSemDiff_carPhone_carLockingEngine_closed_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffClosedWorld(getFD("carPhone"), getFD("carLockingEngine"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "car", "locking", "phone");
  }

  @Test
  public void testSemDiff_carLockingEngine_carLocking_closed_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffClosedWorld(getFD("carLockingEngine"), getFD("carLocking"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "car", "locking", "engine");
  }

  @Test
  public void testSemDiff_carLockingEngine_carPhone_closed_world() {
    Optional<ASTFeatureConfiguration> witness = semDiff
        .semDiffClosedWorld(getFD("carLockingEngine"), getFD("carPhone"));
    assertTrue(witness.isPresent());
    List<String> actual = UnSelectedFeatureCollector.getSelectedFeatures(witness.get());
    assertSelectionEquals(actual, "car", "locking", "engine");
  }

  private ASTFeatureDiagram getFD(String model) {
    return parse(model).getFeatureDiagram();
  }

  private ASTFDCompilationUnit parse(String model) {
    try {
      return parser.parse(BASE_PATH + "/" + model + ".fd").orElseThrow(NoSuchElementException::new);
    }
    catch (IOException | NoSuchElementException e) {
      e.printStackTrace();
    }
    System.exit(1);
    throw new IllegalStateException("Something went wrong..");
  }

}
