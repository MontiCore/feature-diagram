/* (c) https://github.com/MontiCore/monticore */
package featurediagram;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._cocos.FeatureDiagramCoCos;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramGlobalScope;
import featurediagram._symboltable.FeatureDiagramLanguage;
import featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import featurediagram._symboltable.serialization.FeatureDiagramScopeDeSer;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.util.Optional;

public class FeatureDiagramTool {

  public static FeatureDiagramArtifactScope run(String modelFile, ModelPath modelPath) {
    // setup the language infrastructure
    final FeatureDiagramLanguage lang = new FeatureDiagramLanguage();

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);
    Log.info(modelFile + " parsed successfully!", "FeatureDiagramTool");

    // setup the symbol table
    FeatureDiagramArtifactScope modelTopScope = createSymbolTable(lang, modelPath, ast);

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    FeatureDiagramScopeDeSer.store(lang, modelTopScope);
    return modelTopScope;
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTFDCompilationUnit parse(String model) {
    try {
      FeatureDiagramParser parser = new FeatureDiagramParser();
      Optional<ASTFDCompilationUnit> optFD = parser.parse(model);

      if (!parser.hasErrors() && optFD.isPresent()) {
        return optFD.get();
      }
      Log.error("0xFD1000 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFD1001 Failed to parse " + model, e);
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
    FeatureDiagramGlobalScope globalScope = new FeatureDiagramGlobalScope(mp, lang);
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

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
}
