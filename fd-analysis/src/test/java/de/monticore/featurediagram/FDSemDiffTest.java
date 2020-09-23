package de.monticore.featurediagram;

import com.google.common.collect.Sets;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.se_rwth.commons.logging.Log;
import fddiff.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FDSemDiffTest {

  private static final String BASE_PATH = Paths.get("src", "test", "resources", "fddiff").toString();

  private FeatureDiagramParser parser;

  private AST2FD ast2FD;

  private FDSemDiff semDiff;

  @Before
  public void setup() {
    parser = new FeatureDiagramParser();
    ast2FD = new AST2FD();
    semDiff = new FDSemDiff();
    Log.enableFailQuick(false);
  }

  @Test
  public void testSemDiff_car_car() {
    assertFalse(semDiff.semDiff(getFD("car"), getFD("car")).isPresent());
  }

  @Test
  public void testSemDiff_car_car1() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("car"), getFD("car1"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("phone", "engine", "car", "locking", "electric", "fingerprint"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_car_car2() {
    assertFalse(semDiff.semDiff(getFD("car"), getFD("car2")).isPresent());
  }

  @Test
  public void testSemDiff_car1_car() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("car1"), getFD("car"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("engine", "car", "electric", "gas"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_car1_car1() {
    assertFalse(semDiff.semDiff(getFD("car1"), getFD("car1")).isPresent());
  }

  @Test
  public void testSemDiff_car1_car2() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("car1"), getFD("car2"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("engine", "car", "locking", "electric", "fingerprint"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_car2_car() {
    assertFalse(semDiff.semDiff(getFD("car2"), getFD("car")).isPresent());
  }

  @Test
  public void testSemDiff_car2_car1() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("car2"), getFD("car1"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("phone", "engine", "car", "locking", "electric", "fingerprint"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_car2_car2() {
    assertFalse(semDiff.semDiff(getFD("car2"), getFD("car2")).isPresent());
  }

  @Test
  public void testSemDiff_tablet1_tablet1() {
    assertFalse(semDiff.semDiff(getFD("tablet1"), getFD("tablet1")).isPresent());
  }

  @Test
  public void testSemDiff_tablet1_tablet2() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet1"), getFD("tablet2"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "display", "P200", "dis10", "processor", "m64GB", "dis12"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet1_tablet3() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet1"), getFD("tablet3"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "P200", "m64GB", "display", "dis10", "processor"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet2_tablet1() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet2"), getFD("tablet1"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "m256GB", "display", "P100", "processor", "dis12"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet2_tablet2() {
    assertFalse(semDiff.semDiff(getFD("tablet2"), getFD("tablet2")).isPresent());
  }

  @Test
  public void testSemDiff_tablet2_tablet3() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet2"), getFD("tablet3"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "m64GB", "display", "P100", "processor", "dis12"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet3_tablet1() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet3"), getFD("tablet1"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "m256GB", "display", "P100", "processor", "dis11"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet3_tablet2() {
    Optional<FDSemDiffWitness> witness = semDiff.semDiff(getFD("tablet3"), getFD("tablet2"));
    assertTrue(witness.isPresent());
    assertEquals(Sets.newHashSet("tablet", "memory", "m256GB", "display", "P100", "processor", "dis10"),
      witness.get().getWitness().stream().map(Feature::getName).collect(Collectors.toSet()));
  }

  @Test
  public void testSemDiff_tablet3_tablet3() {
    assertFalse(semDiff.semDiff(getFD("tablet3"), getFD("tablet3")).isPresent());
  }


  private FeatureDiagram getFD(String model) {
    return ast2FD.transform(parse(model).getFeatureDiagram());
  }

  private ASTFDCompilationUnit parse(String model) {
    try {
      return parser.parse(BASE_PATH + "/" + model + ".fd").orElseThrow(NoSuchElementException::new);
    } catch (IOException | NoSuchElementException e) {
      e.printStackTrace();
    }
    System.exit(1);
    throw new IllegalStateException("Something went wrong..");
  }

}
