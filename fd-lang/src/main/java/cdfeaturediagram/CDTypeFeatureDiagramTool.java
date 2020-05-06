/* (c) https://github.com/MontiCore/monticore */

package cdfeaturediagram;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import featurediagram.FeatureDiagramTool;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._cocos.FeatureDiagramCoCos;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramLanguage;
import featurediagram._symboltable.FeatureDiagramSymTabMill;
import featurediagram._symboltable.FeatureDiagramSymbolTableCreator;
import featurediagram._symboltable.serialization.FeatureDiagramScopeDeSer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Tool for handling feature diagrams, whose features are classes of a class diagram.
 */
public class CDTypeFeatureDiagramTool {

  /**
   * Use the single argument for specifying the single input feature diagram file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xFD1002 Please specify only one single path to the input model.");
      return;
    }
    FeatureDiagramTool.run(args[0], new ModelPath());
  }

  /**
   * Process a CDFeatureModel with the passed model path entries
   *
   * @param fileName
   * @param modelPaths
   * @return
   */
  public static FeatureDiagramArtifactScope run(String fileName, String... modelPaths) {
    ModelPath mp = new ModelPath();
    for (String entry : modelPaths) {
      mp.addEntry(Paths.get(entry));
    }
    return run(fileName, mp);
  }

  /**
   * Process a CDFeatureModel with the passed model path
   *
   * @param modelFile
   * @param modelPath
   * @return
   */
  public static FeatureDiagramArtifactScope run(String modelFile, ModelPath modelPath) {
    // setup the language infrastructure
    final FeatureDiagramLanguage lang = new FeatureDiagramLanguage();

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);
    Log.info(modelFile + " parsed successfully!", "CDFeatureDiagramTool");

    // setup the symbol table
    FeatureDiagramArtifactScope modelTopScope = createSymbolTable(lang, modelPath, ast);

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    FeatureDiagramScopeDeSer.store(modelTopScope);
    return modelTopScope;
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param fileName - file to parse
   * @return
   */
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

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public static FeatureDiagramArtifactScope createSymbolTable(FeatureDiagramLanguage lang,
      ModelPath mp, ASTFDCompilationUnit ast) {
    FeatureDiagramSymTabMill.initMe(new CDTypeFeatureDiagramSymTabMill());
    CDTypeFeatureDiagramGlobalScope gs = new CDTypeFeatureDiagramGlobalScope(mp);
    FeatureDiagramSymbolTableCreator creator = new FeatureDiagramSymbolTableCreator(gs);
    FeatureDiagramArtifactScope as = creator.createFromAST(ast);
    return as;
  }

}
