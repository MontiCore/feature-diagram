/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FCDValidityTest {
  private final String basedir = "src/test/resources/de/monticore/cdconformance/fcValidity/";
  private ASTFCCompilationUnit fc;
  private ASTFDCompilationUnit fd;

  @BeforeEach
  public void setup() {
    FeatureDiagramMill.init();
    fd = CDxFDConformanceUtil.loadAndCheckFD(basedir + "Reference.fd");
  }

  @Test
  public void testValidConfiguration() {
    fc = CDxFDConformanceUtil.loadAndCheckFc(basedir + "Valid.fc");
    Assertions.assertTrue(FCValidator.checkFcValidity(fd, fc));
  }

  @ParameterizedTest
  @ValueSource(strings = {"NoRootFeature", "InvalidFailingFeature", "InvalidFeature"})
  public void testInvalidConfigurations(String model) {
    fc = CDxFDConformanceUtil.loadAndCheckFc(basedir + model + ".fc");
    Assertions.assertFalse(FCValidator.checkFcValidity(fd, fc));
  }
}
