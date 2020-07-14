/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureConfigurationTool {

  public static final Path SYMBOL_LOCATION = Paths.get("target/symbols");

  protected static final FeatureConfigurationScopeDeSer deser = new FeatureConfigurationScopeDeSer();

  public static FeatureConfigurationArtifactScope run(String modelFile, ModelPath modelPath) {

    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);
    Log.info(modelFile + " parsed successfully!", "FeatureConfigurationTool");

    // setup the symbol table
    FeatureConfigurationArtifactScope modelTopScope = createSymbolTable(modelPath, ast);

    // currently no context conditions exist for feature configurations

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
  public static ASTFCCompilationUnit parse(String model) {
    try {
      FeatureConfigurationParser parser = new FeatureConfigurationParser();
      Optional<ASTFCCompilationUnit> optFC = parser.parse(model);

      if (!parser.hasErrors() && optFC.isPresent()) {
        return optFC.get();
      }
      Log.error("0xFD100 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFD101 Failed to parse " + model, e);
    }
    return null;
  }

  /**
   * Create the symbol table from a model file location
   *
   * @param mp
   * @param model
   * @return
   */
  public static FeatureConfigurationArtifactScope createSymbolTable(ModelPath mp, String model) {
    return createSymbolTable(mp, parse(model));
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public static FeatureConfigurationArtifactScope createSymbolTable(ModelPath mp,
      ASTFCCompilationUnit ast) {
    FeatureConfigurationSymbolTableCreatorDelegator symbolTable = FeatureConfigurationMill
        .featureConfigurationSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(createGlobalScope(mp))
        .build();
    return symbolTable.createFromAST(ast);
  }

  public static FeatureConfigurationGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureConfigurationMill
        .featureConfigurationGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fc")
        .addAdaptedFeatureDiagramSymbolResolvingDelegate(new FeatureDiagramResolvingDelegate(mp))
        .build();
  }

  /**
   * Use the single argument for specifying the single input feature Configuration file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xFD102 Please specify only one single path to the input model.");
      return;
    }
    FeatureConfigurationTool.run(args[0], new ModelPath());
  }
}
