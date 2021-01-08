/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCos;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialDeSer;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialGlobalScope;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramCLI;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
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
 * This tool can process partial feature configuration models both in form of a Java API with individual methods
 * as well as via Command Line Interface (CLI)
 */
public class FeatureConfigurationPartialCLI {

  /**
   * This main method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public static void main(String[] args) {
    FeatureConfigurationPartialCLI cli = new FeatureConfigurationPartialCLI();
    FeatureConfigurationPartialParser parser = new FeatureConfigurationPartialParser();
    FeatureConfigurationPartialDeSer deser = new FeatureConfigurationPartialDeSer();
    Log.initWARN();
    cli.run(args, parser, deser);
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public ASTFCCompilationUnit parse(String model, FeatureConfigurationPartialParser parser) {
    try {
      Optional<ASTFCCompilationUnit> optFC = parser.parse(model);

      if (!parser.hasErrors() && optFC.isPresent()) {
        return optFC.get();
      }
      Log.error("0xFC200 Model could not be parsed.");
    }
    catch (RecognitionException e) {
      Log.error("0xFC201 Failed to parse the partial FC model '" + model + "'. ");
    }
    catch (IOException e) {
      Log.error("0xFC204 Failed to find the file of the partial FC model '" + model + "'.");
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
  public IFeatureConfigurationPartialArtifactScope createSymbolTable(String model,
      ModelPath mp, FeatureConfigurationPartialParser parser) {
    return createSymbolTable(parse(model, parser), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public IFeatureConfigurationPartialArtifactScope createSymbolTable(
      ASTFCCompilationUnit ast, ModelPath mp) {
    IFeatureConfigurationPartialGlobalScope gs = FeatureConfigurationPartialMill.globalScope();
    ModelPaths.merge(gs.getModelPath(), mp);
    ModelPaths.merge(FeatureDiagramMill.globalScope().getModelPath(), mp);

    return FeatureConfigurationPartialMill.scopesGenitorDelegator().createFromAST(ast);
  }

  /**
   * Check all feature configuration partial context conditions against passed ast
   *
   * @param ast
   */
  public void checkCoCos(ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of a partial FC "abc.BasicPhone.fc" and the output
   * path "target" will be: "target/abc/BasicPhone.fcsym"
   *
   * @return
   */
  public String storeSymbols(IFeatureConfigurationPartialArtifactScope scope, Path out,
      FeatureConfigurationPartialDeSer deser) {
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
  public String storeSymbols(IFeatureConfigurationPartialArtifactScope scope,
      String symbolFileName, FeatureConfigurationPartialDeSer deser) {
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
  public ASTFeatureConfiguration run(String modelFile, ModelPath mp,
      FeatureConfigurationPartialParser parser, FeatureConfigurationPartialDeSer deser) {

    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile, parser);

    // setup the symbol table
    createSymbolTable(ast, mp);

    // check context conditions
    checkCoCos(ast);

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
  public ASTFeatureConfiguration run(String modelFile, FeatureConfigurationPartialParser parser,
      FeatureConfigurationPartialDeSer deser) {
    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile, parser);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    ModelPath mp = new ModelPath(path, FeatureDiagramCLI.SYMBOL_OUT);
    createSymbolTable(ast, mp);

    // check context conditions
    checkCoCos(ast);

    // do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * This method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public void run(String[] args, FeatureConfigurationPartialParser parser,
      FeatureConfigurationPartialDeSer deser) {
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new BasicParser();
      CommandLine cmd = cliParser.parse(options, args);
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
        for (String p : cmd.getOptionValues("path")) {
          mp.addEntry(Paths.get(p));
        }
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
      ASTFCCompilationUnit ast = parse(input, parser);
      IFeatureConfigurationPartialArtifactScope symbolTable = createSymbolTable(ast, mp);
      checkCoCos(ast);

      // print or store symbol table
      if (cmd.hasOption("symboltable")) {
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          // store symbol table to passed file
          JsonPrinter.disableIndentation();
          String symbolFile = output.resolve(s).toString();
          storeSymbols(symbolTable, symbolFile, deser);
        }
        else {
          //print (formatted!) symboltable to console
          JsonPrinter.enableIndentation();
          System.out.println(deser.serialize(symbolTable));
        }
      }

      // pretty print  model and either output on stdout or store to file
      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureConfigurationPartialPrettyPrinter.print(ast);
        String outFile = cmd.getOptionValue("prettyprint");
        if (null != outFile) {
          FileReaderWriter.storeInFile(output.resolve(outFile), prettyPrinted);
        }
        else {
          System.out.println(prettyPrinted);
        }
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FeatureConfigurationPartialTool.jar", options, true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

  /**
   * Initialize options of the CLI
   *
   * @return
   */
  protected Options initOptions() {
    Options options = new Options();
    options.addOption("h", "help", false, "Prints this help dialog");
    options.addOption("i", "input", true,
        "Reads the (mandatory) source file resp. the contents of the model");
    options.addOption("o", "output", true, "Path of generated files");

    Option modelPath = new Option("path", true, "Sets the artifact path for imported symbols");
    modelPath.setArgs(Option.UNLIMITED_VALUES);
    modelPath.setValueSeparator(' ');
    options.addOption(modelPath);

    Option symboltable = new Option("s", true,
        "Serializes and prints the symbol table to stdout or a specified output file");
    symboltable.setOptionalArg(true);
    symboltable.setLongOpt("symboltable");
    options.addOption(symboltable);

    Option prettyprint = new Option("pp", true,
        "Prints the AST to stdout or a specified output file");
    prettyprint.setOptionalArg(true);
    prettyprint.setLongOpt("prettyprint");
    options.addOption(prettyprint);

    return options;
  }

}
