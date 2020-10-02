/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;
import test.AbstractTest;

import java.nio.file.Paths;

public class FeatureConfigurationCoCoTest extends AbstractTest {

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
    fcTool.createSymbolTable(ast, mp, fcDeSer);
    return ast;
  }

}
