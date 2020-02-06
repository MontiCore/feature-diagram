package fd;

import cdfeaturediagram.CDTypeFeatureDiagramGlobalScope;
import de.monticore.io.paths.ModelPath;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class CD4AFDSymbolTableTest {

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile, String path)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureDiagramLanguage lang = new FeatureDiagramLanguage();
    CDTypeFeatureDiagramGlobalScope globalScope = new CDTypeFeatureDiagramGlobalScope(
        new ModelPath(Paths.get(path)), lang);
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

  @Test
  public void test() throws IOException {
    String model = "src/test/resources/cd4a-fd/FooBar.fd";
    FeatureDiagramArtifactScope scope = setupSymbolTable(model, "src/test/resources/cd4a-fd");

    assertTrue(null != scope);
    FeatureDiagramSymbol fd = scope.resolveFeatureDiagram("FooBar").orElse(null);
    assertTrue(null != fd);

    List<FeatureSymbol> foo = fd.getSpannedScope().resolveFeatureMany("Foo");
    assertEquals(2, foo.size());
    assertEquals("blo", foo.get(0).getClass());
  }

}
