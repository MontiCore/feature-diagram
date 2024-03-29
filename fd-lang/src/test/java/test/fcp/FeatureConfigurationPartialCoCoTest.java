/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.io.paths.MCPath;
import org.junit.Before;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Paths;

public class FeatureConfigurationPartialCoCoTest extends AbstractLangTest {

  @Before
  public void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  @Test
  public void testWrongBlock() {
    ASTFCCompilationUnit ast = setupSymbolTable("WrongBlock.fc");
    fcpTool.runDefaultCoCos(ast);
    assertErrorCode("0xFC203");
  }

  protected ASTFCCompilationUnit setupSymbolTable(String modelFile) {
    MCPath mp = new MCPath(Paths.get("src/test/resources"));
    ASTFCCompilationUnit ast = fcpTool
        .parse("src/test/resources/pfcinvalid/" + modelFile);
    fcpTool.createSymbolTable(ast, mp);
    return ast;
  }
}
