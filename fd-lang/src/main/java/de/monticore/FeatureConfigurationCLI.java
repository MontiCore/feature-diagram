/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbols2Json;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationGlobalScope;
import de.monticore.featureconfiguration.prettyprint.FeatureConfigurationPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
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
 * This tool can process feature configuration models both in form of a Java API with individual methods
 * as well as via Command Line Interface (CLI)
 */
@Deprecated
public class FeatureConfigurationCLI {

  /**
   * This main method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public static void main(String[] args) {
    FeatureConfigurationCLI cli = new FeatureConfigurationCLI();
    FeatureConfigurationParser parser = new FeatureConfigurationParser();
    FeatureConfigurationSymbols2Json symbols2J = new FeatureConfigurationSymbols2Json();
    Log.initWARN();
    cli.run(args, parser, symbols2J);
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public ASTFCCompilationUnit parse(String model, FeatureConfigurationParser parser) {
    try {
      Optional<ASTFCCompilationUnit> optFC = parser.parse(model);

      if (!parser.hasErrors() && optFC.isPresent()) {
        Log.info(model + " parsed successfully!", "FeatureConfigurationTool");
        return optFC.get();
      }
      Log.error("0xFC100 Model could not be parsed.");
    }
    catch (RecognitionException  e) {
      Log.error("0xFC101 Failed to parse the FC model '" + model +"'. ");
    }
    catch (IOException e) {
      Log.error("0xFC104 Failed to find the file of the FC model '" + model +"'.");
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
  public IFeatureConfigurationArtifactScope createSymbolTable(String model, MCPath mp,
      FeatureConfigurationParser parser) {
    return createSymbolTable(parse(model, parser), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public IFeatureConfigurationArtifactScope createSymbolTable(ASTFCCompilationUnit ast,
      MCPath mp) {
    IFeatureConfigurationGlobalScope gs = FeatureConfigurationMill.globalScope();
    ModelPaths.merge(gs.getSymbolPath(), mp);
    ModelPaths.merge(FeatureDiagramMill.globalScope().getSymbolPath(), mp);
    return FeatureConfigurationMill.scopesGenitorDelegator().createFromAST(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of an FC "abc.BasicPhone.fc" and the output
   * path "target" will be: "target/abc/BasicPhone.fcsym"
   *
   * @return
   */
  public String storeSymbols(IFeatureConfigurationArtifactScope scope, Path out,
      FeatureConfigurationSymbols2Json symbols2Json) {
    Path f = out
        .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
        .resolve(scope.getName() + ".fcsym");
    String serialized = symbols2Json.serialize(scope);
    FileReaderWriter.storeInFile(f, serialized);
    return serialized;
  }

  /**
   * stores the symbol table of a passed ast in a file at the passed symbolFileName
   *
   * @return
   */
  public String storeSymbols(IFeatureConfigurationArtifactScope scope,
      String symbolFileName, FeatureConfigurationSymbols2Json symbols2Json) {
    String serialized = symbols2Json.serialize(scope);
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
  public ASTFeatureConfiguration run(String modelFile, MCPath mp,
      FeatureConfigurationParser parser) {

    // parse the model and create the AST representation
    ASTFCCompilationUnit ast = parse(modelFile, parser);

    // setup the symbol table
    createSymbolTable(ast, mp);

    // currently no context conditions exist for feature configurations.
    // Also, do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * Processes a feature configuration (parsing, symbol table creation, and type check,
   * symbol table is not stored here). Searches for feature models that are located in files
   * in the same directory as the passed FC.
   *
   * @param modelFile
   * @return
   */
  public ASTFeatureConfiguration run(String modelFile, FeatureConfigurationParser parser) {
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
    MCPath mp = new MCPath(path, FeatureDiagramCLI.SYMBOL_OUT);
    createSymbolTable(ast, mp);

    // currently no context conditions exist for feature configurations.
    // Also, do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * This method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public void run (String args[]) {
    FeatureConfigurationParser parser = new FeatureConfigurationParser();
    FeatureConfigurationSymbols2Json symbols2J = new FeatureConfigurationSymbols2Json();
    run(args, parser, symbols2J);
  }

  /**
   * This method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public void run(String[] args, FeatureConfigurationParser parser,
      FeatureConfigurationSymbols2Json symbols2Json) {
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new BasicParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FeatureConfigurationTool.jar", options, true);
        return;
      }

      //Set input file and parse it
      if (!cmd.hasOption("input")) {
        Log.error(
            "0xFC102 The input file is a mandatory argument of the FeatureConfigurationTool!");
      }
      String input = cmd.getOptionValue("input");

      //Set path for imported symbols
      MCPath mp = new MCPath();
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

      // parse and create symbol table, FeatureConfiguration langage has no CoCos
      ASTFCCompilationUnit ast = parse(input, parser);
      IFeatureConfigurationArtifactScope symbolTable = createSymbolTable(ast, mp);

      // print or store symbol table
      if (cmd.hasOption("symboltable")) {
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          // store symbol table to passed file
          JsonPrinter.disableIndentation();
          String symbolFile = output.resolve(s).toString();
          storeSymbols(symbolTable, symbolFile, symbols2Json);
        }
        else {
          //print (formatted!) symboltable to console
          JsonPrinter.enableIndentation();
          System.out.println(symbols2Json.serialize(symbolTable));
        }
      }

      // pretty print  model and either output on stdout or store to file
      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureConfigurationPrinter.print(ast);
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
      formatter.printHelp("java -jar FeatureConfigurationTool.jar", options, true);
      Log.error("0xFC103 An exception occured while processing the CLI input!", e);
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