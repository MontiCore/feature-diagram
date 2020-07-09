/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramScopeDeSer;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureDiagramTool {

  public static final Path SYMBOL_LOCATION = Paths.get("target/symbols");

  protected static final FeatureDiagramScopeDeSer deser = new FeatureDiagramScopeDeSer();

  public static FeatureDiagramArtifactScope run(String modelFile, ModelPath modelPath) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);
    Log.info(modelFile + " parsed successfully!", "FeatureDiagramTool");

    // setup the symbol table
    FeatureDiagramArtifactScope modelTopScope = createSymbolTable(modelPath, ast);

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    deser.store(modelTopScope, SYMBOL_LOCATION);

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
      Log.error("0xFD100 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFD101 Failed to parse " + model, e);
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
  public static FeatureDiagramArtifactScope createSymbolTable(ModelPath mp,
      ASTFDCompilationUnit ast) {
    FeatureDiagramGlobalScope globalScope = FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fd")
        .build();

    FeatureDiagramSymbolTableCreatorDelegator symbolTable = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    return symbolTable.createFromAST(ast);
  }

  /**
   * Use the single argument for specifying the single input feature diagram file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xFD102 Please specify only one single path to the input model.");
      return;
    }
    FeatureDiagramTool.run(args[0], new ModelPath());
  }
}
