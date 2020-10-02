/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialCLI;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;
import test.AbstractTest;

import java.nio.file.Paths;

public class FeatureConfigurationPartialCoCoTest extends AbstractTest {

  @Test
  public void testWrongBlock() {
    ASTFCCompilationUnit ast = setupSymbolTable("WrongBlock.fc");
    new FeatureConfigurationPartialCLI().checkCoCos(ast);
    assertErrorCode("0xFC203");
  }

  protected ASTFCCompilationUnit setupSymbolTable(String modelFile) {
    FeatureConfigurationPartialCLI tool = new FeatureConfigurationPartialCLI();
    ModelPath mp = new ModelPath(Paths.get("src/test/resources"));
    ASTFCCompilationUnit ast = tool.parse("src/test/resources/pfcinvalid/" + modelFile);
    tool.createSymbolTable(ast, mp);
    return ast;
  }
}
