/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.io.paths.ModelPath;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Paths;

public class FeatureConfigurationCoCoTest extends AbstractLangTest {

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationMill.init();
  }

  @Test
  public void testInvalidFD() {
    setupSymbolTable("InvalidFD.fc");
    assertErrorCode("0xFC002");
  }

  @Test
  public void testInvalidStarImport() {
    setupSymbolTable("InvalidStarImport.fc");
    assertErrorCode("0xFC002");
  }

  @Test
  public void testInvalidImport() {
    setupSymbolTable("InvalidImport.fc");
    assertErrorCode("0xFC011");
  }

  @Test
  public void testMultipleImport() {
    setupSymbolTable("MultipleImport.fc");
    assertErrorCode("0xFC010");
  }

  @Test
  public void testFeatureDoesNotExist() {
    setupSymbolTable("InvalidFeature.fc");
    assertErrorCode("0xFC001");
  }

  protected ASTFCCompilationUnit setupSymbolTable(String modelFile) {
    ASTFCCompilationUnit ast = fcTool.parse("src/test/resources/fcinvalid/" + modelFile, fcParser);
    ModelPath mp = new ModelPath(Paths.get("src/test/resources"));
    fcTool.createSymbolTable(ast, mp);
    return ast;
  }

}
