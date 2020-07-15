/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureDiagramResolvingDelegate;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCoChecker;
import de.monticore.featureconfigurationpartial._cocos.UseSelectBlock;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialGlobalScope;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbolTableCreatorDelegator;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureConfigurationPartialTool {

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
    String modelFile = args[0];
    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);
    Log.info(modelFile + " parsed successfully!", "FeatureConfigurationPartialTool");

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).getParent();
    if(ast.isPresentPackage()){
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    createSymbolTable(new ModelPath(path), ast);

    // check context conditions for partial feature configurations
    FeatureConfigurationPartialCoCoChecker checker = new FeatureConfigurationPartialCoCoChecker();
    checker.addCoCo(new UseSelectBlock());
    checker.checkAll(ast);

    // do not store artifact scope

  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTFCCompilationUnit parse(String model) {
    try {
      FeatureConfigurationPartialParser parser = new FeatureConfigurationPartialParser();
      Optional<ASTFCCompilationUnit> optFC = parser.parse(model);

      if (!parser.hasErrors() && optFC.isPresent()) {
        return optFC.get();
      }
      Log.error("0xFC200 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFC201 Failed to parse " + model, e);
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
  public static FeatureConfigurationPartialArtifactScope createSymbolTable(String model, ModelPath mp) {
    return createSymbolTable(mp, parse(model));
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public static FeatureConfigurationPartialArtifactScope createSymbolTable(ModelPath mp,
      ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialSymbolTableCreatorDelegator symbolTable = FeatureConfigurationPartialMill
        .featureConfigurationPartialSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(createGlobalScope(mp))
        .build();
    return symbolTable.createFromAST(ast);
  }

  public static FeatureConfigurationPartialGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureConfigurationPartialMill
        .featureConfigurationPartialGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fc")
        .addAdaptedFeatureDiagramSymbolResolvingDelegate(new FeatureDiagramResolvingDelegate(mp))
        .build();
  }

}
