/* (c) https://github.com/MontiCore/monticore */

package cdfeaturediagram;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._cocos.FeatureDiagramCoCos;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramSymTabMill;
import featurediagram._symboltable.FeatureDiagramSymbolTableCreator;
import featurediagram._symboltable.serialization.FeatureDiagramScopeDeSer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CDTypeFeatureDiagramTool {

  public static ASTFDCompilationUnit parse(String fileName) {
    FeatureDiagramParser parser = new FeatureDiagramParser();
    try {
      Optional<ASTFDCompilationUnit> parseResult = parser.parse(fileName);
      if (parseResult.isPresent()) {
        return parseResult.get();
      }
      Log.error("0xFD0011 Error while parsing '" + fileName + "'!");
    }
    catch (IOException e) {
      Log.error("0xFD0012 Error while parsing '" + fileName + "'!", e);
    }
    return null;
  }

  public static FeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast,
      ModelPath mp) {
    FeatureDiagramSymTabMill.initMe(new CDTypeFeatureDiagramSymTabMill());
    CDTypeFeatureDiagramGlobalScope gs = new CDTypeFeatureDiagramGlobalScope(mp);
    FeatureDiagramSymbolTableCreator creator = new FeatureDiagramSymbolTableCreator(gs);
    FeatureDiagramArtifactScope as = creator.createFromAST(ast);
    return as;
  }

  public static void checkCoCos(ASTFDCompilationUnit ast) {
    FeatureDiagramCoCos.checkAll(ast);
  }

  public static FeatureDiagramArtifactScope run(String fileName, String... modelPaths) {
    ModelPath mp = new ModelPath();
    for (String entry : modelPaths) {
      mp.addEntry(Paths.get(entry));
    }
    return run(fileName, mp);
  }

  public static FeatureDiagramArtifactScope run(String fileName, ModelPath mp) {
    ASTFDCompilationUnit ast = parse(fileName);
    FeatureDiagramArtifactScope scope = createSymbolTable(ast, mp);
    checkCoCos(ast);
//    new FeatureDiagramScopeDeSer().store(scope, Paths.get("target/symbols")); //TODO implement desers
    return scope;
  }

}
