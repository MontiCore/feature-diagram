/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import static de.monticore.cdconformance.CDConfParameter.NAME_MAPPING;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CDxFDConformanceTest {
  private final String basedir = "src/test/resources/de/monticore/cdconformance/";
  private ASTFDCompilationUnit fd;
  ASTCDCompilationUnit refCD;
  Set<CDConfParameter> params;

  public void initMills() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    FeatureDiagramMill.init();
  }

  @BeforeEach
  public void setup() {
    initMills();
    params = new HashSet<>(Set.of(NAME_MAPPING));
    refCD = CDxFDConformanceUtil.loadAndCheckCD(basedir + "Reference.cd");
    fd = CDxFDConformanceUtil.loadAndCheckFD(basedir + "Reference.fd");
  }

  @ParameterizedTest
  @ValueSource(strings = {"WithoutClazz", "AllFeatures", "WithClazz"})
  public void testConformClassDiagrams(String concrete) {
    ASTCDCompilationUnit conCD =
        CDxFDConformanceUtil.loadAndCheckCD(basedir + "valid/" + concrete + ".cd");
    Assertions.assertTrue(CDxFDConformance.checkConformance(refCD, conCD, fd, "ref", params));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Excludes", "WithClazz", "WithoutClazz"})
  public void testNotConformClassDiagrams(String concrete) {
    ASTCDCompilationUnit conCD =
        CDxFDConformanceUtil.loadAndCheckCD(basedir + "invalid/" + concrete + ".cd");
    Assertions.assertFalse(CDxFDConformance.checkConformance(refCD, conCD, fd, "ref", params));
  }
}
