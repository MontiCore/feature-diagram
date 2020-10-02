/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramScopeDeSer;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.featurediagram.prettyprint.FeatureDiagramPrettyPrinter;
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
 * This tool can process feature diagram models both in form of a Java API with individual methods
 * as well as via Command Line Interface (CLI)
 */
public class FeatureDiagramCLI {

  /**
   * This main method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public static void main(String[] args) {
    FeatureDiagramCLI cli = new FeatureDiagramCLI();
    FeatureDiagramParser parser = new FeatureDiagramParser();
    FeatureDiagramScopeDeSer deser = new FeatureDiagramScopeDeSer();
    Log.initWARN();
    cli.run(args, parser, deser);
  }

  /**
   * constant for the default folder into which symbols are stored
   */
  public static final Path SYMBOL_OUT = Paths.get("target/symbols");

  /**
   * Parse the model contained in the specified file and return the created AST.
   *
   * @param model - file to parse
   * @return
   */
  public ASTFDCompilationUnit parse(String model, FeatureDiagramParser parser) {
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
   * Create the symbol table from a model file location and returns the produced artifact scope
   *
   * @param model
   * @param mp
   * @return
   */
  public IFeatureDiagramArtifactScope createSymbolTable(String model, ModelPath mp, FeatureDiagramParser parser) {
    return createSymbolTable(parse(model, parser), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @param mp
   * @return
   */
  public IFeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast, ModelPath mp) {
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
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
  public IFeatureDiagramGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fd")
        .build();
  }

  /**
   * Check all feature diagram context conditions against the ast passed as argument
   *
   * @param ast
   */
  public void checkCoCos(ASTFDCompilationUnit ast) {
    FeatureDiagramCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of an FD "abc.Phone.fd" and the output
   * path "target" will be: "target/abc/Phone.fdsym"
   *
   * @return
   */
  public String storeSymbols(IFeatureDiagramArtifactScope scope, Path out, FeatureDiagramScopeDeSer deser) {
    Path f = out
        .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
        .resolve(scope.getName()+".fdsym");
    String serialized = deser.serialize(scope);
    FileReaderWriter.storeInFile(f, serialized);
    return serialized;
  }

  /**
   * stores the symbol table of a passed ast in a file at the passed symbolFileName
   *
   * @return
   */
  public String storeSymbols(IFeatureDiagramArtifactScope scope, String symbolFileName, FeatureDiagramScopeDeSer deser) {
    String serialized = deser.serialize(scope);
    FileReaderWriter.storeInFile(Paths.get(symbolFileName), serialized);
    return serialized;
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @param path
   * @return
   */
  public ASTFeatureDiagram run(String modelFile, Path out, ModelPath path, FeatureDiagramParser parser, FeatureDiagramScopeDeSer deser) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile, parser);

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast, path);

    // execute default context conditions
    checkCoCos(ast);

    // store artifact scope after context conditions have been checked
    storeSymbols(modelTopScope, out, deser);

    return ast.getFeatureDiagram();
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table) and stores symbols at default location
   *
   * @param modelFile
   * @param path
   * @return
   */
  public ASTFeatureDiagram run(String modelFile, ModelPath path, FeatureDiagramParser parser, FeatureDiagramScopeDeSer deser) {
    return run(modelFile, SYMBOL_OUT, path, parser, deser);
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @return
   */
  public ASTFeatureDiagram run(String modelFile, FeatureDiagramParser parser, FeatureDiagramScopeDeSer deser) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile, parser);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast,
        new ModelPath(path, SYMBOL_OUT));

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    storeSymbols(modelTopScope, SYMBOL_OUT, deser);

    return ast.getFeatureDiagram();
  }

  /**
   * This method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public void run(String[] args, FeatureDiagramParser parser, FeatureDiagramScopeDeSer deser) {
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new BasicParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FeatureDiagramTool.jar", options, true);
        return;
      }

      //Set input file and parse it
      if (!cmd.hasOption("input")) {
        Log.error("0xFD102 The input file is a mandatory argument of the FeatureDiagramTool!");
      }
      String input = cmd.getOptionValue("input");

      //Set path for imported symbols
      ModelPath mp = new ModelPath();
      if (cmd.hasOption("path")) {
        for(String p : cmd.getOptionValue("path").split(":")){
          mp.addEntry(Paths.get(p));
        }
      }

      //Set output path for stored symbols and pretty printed models (or use default)
      Path output = Paths.get("target");
      if (cmd.hasOption("output")) {
        output = Paths.get(cmd.getOptionValue("output"));
      }

      // parse, create symbol table, check all cocos
      ASTFDCompilationUnit ast = parse(input, parser);
      IFeatureDiagramArtifactScope symbolTable = createSymbolTable(ast, mp);
      checkCoCos(ast);

      // print (and optionally store) symbol table
      if (cmd.hasOption("symboltable")) {
        JsonPrinter.disableIndentation();
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          String symbolFile = output.resolve(s).toString();
          storeSymbols(symbolTable, symbolFile, deser);
        }
        else {
          storeSymbols(symbolTable, output, deser);
        }

        //print (formatted!) symboltable to console
        JsonPrinter.enableIndentation();
        System.out.println(deser.serialize(symbolTable));
      }

      // print (and optionally store) model
      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureDiagramPrettyPrinter.print(ast);
        System.out.println(prettyPrinted);
        String outFile = cmd.getOptionValue("prettyprint");
        if (null != outFile) {
          FileReaderWriter.storeInFile(output.resolve(outFile), prettyPrinted);
        }
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FeatureDiagramTool.jar", options, true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

  /**
   * Initialize options of the CLI
   * @return
   */
  protected Options initOptions() {
    Options options = new Options();
    options.addOption("h", "help", false, "Prints this help dialog");
    options.addOption("i", "input", true, "Reads the (mandatory) source file resp. the contents of the model");
    options.addOption("o", "output", true, "Path of generated files");

    Option modelPath = new Option("path", true, "Sets the artifact path for imported symbols");
    modelPath.setArgs(Option.UNLIMITED_VALUES);
    modelPath.setValueSeparator(':');
    options.addOption(modelPath);

    Option symboltable = new Option("s", true,"Serializes and prints the symbol table to stdout, if present, the specified output file");
    symboltable.setOptionalArg(true);
    symboltable.setLongOpt("symboltable");
    options.addOption(symboltable);

    Option prettyprint = new Option("pp", true, "Prints the AST to stdout and, if present, the specified output file");
    prettyprint.setOptionalArg(true);
    prettyprint.setLongOpt("prettyprint");
    options.addOption(prettyprint);

    return options;
  }

}