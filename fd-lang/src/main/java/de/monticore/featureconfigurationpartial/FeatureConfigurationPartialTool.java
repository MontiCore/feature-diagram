/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._symboltable.FeatureDiagramResolvingDelegate;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCos;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialScopeDeSer;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbolTableCreatorDelegator;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialGlobalScope;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This tool can process partial feature configuration models both in form of a Java API with static methods
 * as well as via Command Line Interface (CLI)
 */
public class FeatureConfigurationPartialTool {

  protected static final FeatureConfigurationPartialScopeDeSer deser = new FeatureConfigurationPartialScopeDeSer();

  protected static final FeatureConfigurationPartialParser parser = new FeatureConfigurationPartialParser();

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTFCCompilationUnit parse(String model) {
    try {
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
  public static IFeatureConfigurationPartialArtifactScope createSymbolTable(String model,
      ModelPath mp) {
    return createSymbolTable(parse(model), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public static IFeatureConfigurationPartialArtifactScope createSymbolTable(
      ASTFCCompilationUnit ast, ModelPath mp) {
    FeatureConfigurationPartialSymbolTableCreatorDelegator symbolTable = FeatureConfigurationPartialMill
        .featureConfigurationPartialSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(createGlobalScope(mp))
        .build();
    return symbolTable.createFromAST(ast);
  }

  /**
   * short-hand for creating a global scope via mill
   *
   * @param mp
   * @return
   */
  public static IFeatureConfigurationPartialGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureConfigurationPartialMill
        .featureConfigurationPartialGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fc")
        .addAdaptedFeatureDiagramSymbolResolvingDelegate(new FeatureDiagramResolvingDelegate(mp))
        .build();
  }

  /**
   * Check all feature configuration partial context conditions against passed ast
   *
   * @param ast
   */
  public static void checkCoCos(ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of a partial FC "abc.BasicPhone.fc" and the output
   * path "target" will be: "target/abc/BasicPhone.fcsym"
   *
   * @return
   */
  public static String storeSymbols(IFeatureConfigurationPartialArtifactScope scope, Path out) {
    Path f = out
        .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
        .resolve(scope.getName() + ".fcsym");
    String serialized = deser.serialize(scope);
    FileReaderWriter.storeInFile(f, serialized);
    return serialized;
  }

  /**
   * stores the symbol table of a passed ast in a file at the passed symbolFileName
   *
   * @return
   */
  public static String storeSymbols(IFeatureConfigurationPartialArtifactScope scope,
      String symbolFileName) {
    String serialized = deser.serialize(scope);
    FileReaderWriter.storeInFile(Paths.get(symbolFileName), serialized);
    return serialized;
  }

  /**
   * Processes a feature configuration (parsing, symbol table creation, and type check,
   * symbol table is not stored here) with the passed modelpath
   *
   * @param modelFile
   * @param mp
   * @return
   */
  public static ASTFeatureConfiguration run(String modelFile, ModelPath mp) {

    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = FeatureConfigurationPartialTool.parse(modelFile);

    // setup the symbol table
    FeatureConfigurationPartialTool.createSymbolTable(ast, mp);

    // check context conditions
    FeatureConfigurationPartialTool.checkCoCos(ast);

    // do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * Processes a feature configuration (parsing, symbol table creation, and type check,
   * symbol table is not stored here). Searches for feature models that are located in files
   * in the same directory as the passed partial FC.
   *
   * @param modelFile
   * @return
   */
  public static ASTFeatureConfiguration run(String modelFile) {
    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = FeatureConfigurationPartialTool.parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    ModelPath mp = new ModelPath(path, FeatureDiagramTool.SYMBOL_OUT);
    FeatureConfigurationPartialTool.createSymbolTable(ast, mp);

    // check context conditions
    FeatureConfigurationPartialTool.checkCoCos(ast);

    // do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * This main method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public static void main(String[] args) {
    //reuse the CLI options from FeatureConfigurationTool
    Options options = FeatureDiagramTool.getOptions();
    Log.initWARN();

    try {
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FeatureConfigurationPartialTool.jar", options, true);
        return;
      }

      //Set input file and parse it
      if (!cmd.hasOption("input")) {
        Log.error(
            "0xFE102 The input file is a mandatory argument of the FeatureConfigurationPartialTool!");
      }
      String input = cmd.getOptionValue("input");

      //Set path for imported symbols
      ModelPath mp = new ModelPath();
      if (cmd.hasOption("path")) {
        mp.addEntry(Paths.get(cmd.getOptionValue("path")));
      }
      else {
        //else use location in which input model is located as model path
        Path modelFolder = Paths.get(input).toAbsolutePath().getParent();
        mp.addEntry(modelFolder);
      }

      //Set output path for stored symbols and pretty printed models (or use default)
      Path output = Paths.get("target");
      if (cmd.hasOption("output")) {
        output = Paths.get(cmd.getOptionValue("output"));
      }

      // parse, create symbol table, check all cocos
      ASTFCCompilationUnit ast = FeatureConfigurationPartialTool.parse(input);
      IFeatureConfigurationPartialArtifactScope symbolTable = FeatureConfigurationPartialTool
          .createSymbolTable(ast, mp);
      FeatureConfigurationPartialTool.checkCoCos(ast);

      // print (and optionally store) symbol table
      if (cmd.hasOption("symboltable")) {
        JsonPrinter.disableIndentation();
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          String symbolFile = output.resolve(s).toString();
          FeatureConfigurationPartialTool.storeSymbols(symbolTable, symbolFile);
        }
        else {
          FeatureConfigurationPartialTool.storeSymbols(symbolTable, output);
        }

        //print (formatted!) symboltable to console
        JsonPrinter.enableIndentation();
        System.out.println(deser.serialize(symbolTable));
      }

      // print (and optionally store) model
      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureConfigurationPartialPrettyPrinter.print(ast);
        System.out.println(prettyPrinted);
        String outFile = cmd.getOptionValue("prettyprint");
        if (null != outFile) {
          FileReaderWriter.storeInFile(output.resolve(outFile), prettyPrinted);
        }
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FeatureConfigurationPartialTool.jar", options, true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

}
