/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import featurediagram._symboltable.serialization.FeatureDiagramScopeDeSer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FeatureDiagramDeSerTest {

  @BeforeClass
  public static void disableFailQuick() {
//        Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
  }

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile, ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureDiagramLanguage lang = new FeatureDiagramLanguage();
    FeatureDiagramGlobalScope globalScope = new FeatureDiagramGlobalScope(mp, lang);
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile)
      throws IOException {
    return setupSymbolTable(modelFile, new ModelPath());
  }

  @Test
  public void testSerializeDeserialize() throws IOException {
    String model = "src/test/resources/fdvalid/BasicElements.fd";
    FeatureDiagramArtifactScope scope = setupSymbolTable(model);
    assertTrue(null != scope);
    String serialized = new FeatureDiagramScopeDeSer().serialize(scope);
    System.out.println(serialized);
    assertTrue(null != serialized);

    FeatureDiagramGlobalScope gs = FeatureDiagramSymTabMill
        .featureDiagramGlobalScopeBuilder()
        .setFeatureDiagramLanguage(new FeatureDiagramLanguage())
        .setModelPath(new ModelPath())
        .build();
    IFeatureDiagramScope deserializedScope = new FeatureDiagramScopeDeSer()
        .deserialize(serialized, gs);
    assertTrue(deserializedScope instanceof FeatureDiagramArtifactScope);
    FeatureDiagramArtifactScope deserialized = (FeatureDiagramArtifactScope) deserializedScope;

    assertEquals(scope.getName(), deserialized.getName());
    assertEquals(scope.getPackageName(), deserialized.getPackageName());
    assertEquals(scope.getImportList().size(), deserialized.getImportList().size());
    assertEquals(scope.getTopLevelSymbol().isPresent(), deserialized.getTopLevelSymbol().isPresent());
    assertEquals(scope.getTopLevelSymbol().get().getName(), deserialized.getTopLevelSymbol().get().getName());
    assertEquals(scope.getLocalFeatureDiagramSymbols().size(), deserialized.getLocalFeatureDiagramSymbols().size());
    assertEquals(scope.getLocalFeatureSymbols().size(), deserialized.getLocalFeatureSymbols().size());

    FeatureDiagramSymbol expectedSymbol = scope.getLocalFeatureDiagramSymbols().get(0);
    FeatureDiagramSymbol actualSymbol = deserialized.getLocalFeatureDiagramSymbols().get(0);
    assertEquals(expectedSymbol.getName(), actualSymbol.getName());
    assertEquals(expectedSymbol.getSpannedScope().getName(), actualSymbol.getSpannedScope().getName());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureSymbols().size(), actualSymbol.getSpannedScope().getLocalFeatureSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSubScopes().size(), actualSymbol.getSpannedScope().getSubScopes().size());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size(), actualSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSpanningSymbol().getName(), actualSymbol.getSpannedScope().getSpanningSymbol().getName());
  }

}
