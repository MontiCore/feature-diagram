/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationGlobalScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbolTableCreatorDelegator;
import de.monticore.featureconfiguration._symboltable.FeatureDiagramResolvingDelegate;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureConfigurationTool {

  /**
   * Use the single argument for specifying the single input feature configuration file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xFC102 Please specify only one single path to the input model.");
      return;
    }
    run(args[0]);
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
        Log.info(model + " parsed successfully!", "FeatureConfigurationTool");
        return optFC.get();
      }
      Log.error("0xFC100 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFC101 Failed to parse " + model, e);
    }
    return null;
  }

  /**
   * Create the symbol table from a model file location
   *
   * @param model
   * @param mp
   * @return
   */
  public static FeatureConfigurationArtifactScope createSymbolTable(String model, ModelPath mp) {
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

  public static ASTFeatureConfiguration run(String modelFile, ModelPath mp) {

    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);

    // setup the symbol table
    createSymbolTable(mp, ast);

    // currently no context conditions exist for feature configurations.
    // Also, do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  public static ASTFeatureConfiguration run(String modelFile) {
    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if(ast.isPresentPackage()){
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    createSymbolTable(new ModelPath(path, FeatureDiagramTool.SYMBOL_LOCATION), ast);

    // currently no context conditions exist for feature configurations.
    // Also, do not store artifact scope

    return ast.getFeatureConfiguration();
  }

}
