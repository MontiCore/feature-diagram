/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramScopeDeSer;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureDiagramTool {

  public static final Path SYMBOL_LOCATION = Paths.get("target/symbols");

  protected static final FeatureDiagramScopeDeSer deser = new FeatureDiagramScopeDeSer();

  protected static final FeatureDiagramParser parser = new FeatureDiagramParser();

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
    run(args[0]);
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTFDCompilationUnit parse(String model) {
    try {
      Optional<ASTFDCompilationUnit> optFD = parser.parse(model);

      if (!parser.hasErrors() && optFD.isPresent()) {
        Log.info(model + " parsed successfully!", "FeatureDiagramTool");
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
   * Create the symbol table from a model file location
   *
   * @param model
   * @param mp
   * @return
   */
  public static FeatureDiagramArtifactScope createSymbolTable(String model, ModelPath mp) {
    return createSymbolTable(parse(model), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @param mp
   * @return
   */
  public static FeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast,
      ModelPath mp) {
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(createGlobalScope(mp))
        .build();
    return symbolTable.createFromAST(ast);
  }

  public static FeatureDiagramGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fd")
        .build();
  }

  public static void checkCoCos(ASTFDCompilationUnit ast) {
    FeatureDiagramCoCos.checkAll(ast);
  }

  public static File storeSymbols(ASTFDCompilationUnit ast, String fileName) {
    File f = new File(fileName);
    FileReaderWriter.storeInFile(f.toPath(), deser.serialize(ast.getEnclosingScope()));
    return f;
  }

  public static ASTFeatureDiagram run(String modelFile, ModelPath mp) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    // setup the symbol table
    FeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast, mp);

    // execute default context conditions
    checkCoCos(ast);

    // store artifact scope after context conditions have been checked
    deser.store(modelTopScope, SYMBOL_LOCATION);

    return ast.getFeatureDiagram();
  }

  public static ASTFeatureDiagram run(String modelFile) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    FeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast, new ModelPath(path));

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    deser.store(modelTopScope, SYMBOL_LOCATION);

    return ast.getFeatureDiagram();
  }

}
