/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.io.paths.MCPath;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Paths;

public class FeatureConfigurationPartialCoCoTest extends AbstractLangTest {

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  @Test
  public void testWrongBlock() {
    ASTFCCompilationUnit ast = setupSymbolTable("WrongBlock.fc");
    fcpTool.checkCoCos(ast);
    assertErrorCode("0xFC203");
  }

  protected ASTFCCompilationUnit setupSymbolTable(String modelFile) {
    MCPath mp = new MCPath(Paths.get("src/test/resources"));
    ASTFCCompilationUnit ast = fcpTool
        .parse("src/test/resources/pfcinvalid/" + modelFile, fcpParser);
    fcpTool.createSymbolTable(ast, mp);
    return ast;
  }
}
