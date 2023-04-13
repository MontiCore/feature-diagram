/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCos;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbols2Json;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialGlobalScope;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FeatureConfigurationPartialTool extends FeatureConfigurationPartialToolTOP {

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public IFeatureConfigurationPartialArtifactScope createSymbolTable(
    ASTFCCompilationUnit ast, MCPath mp) {
    IFeatureConfigurationPartialGlobalScope gs = FeatureConfigurationPartialMill.globalScope();
    ModelPaths.merge(gs.getSymbolPath(), mp);
    ModelPaths.merge(FeatureDiagramMill.globalScope().getSymbolPath(), mp);

    return FeatureConfigurationPartialMill.scopesGenitorDelegator().createFromAST(ast);
  }

  @Override
  public IFeatureConfigurationPartialArtifactScope createSymbolTable(ASTFCCompilationUnit node) {
    Log.warn("0xFD113 Please use the createSymbolTable method with two parameters");
    return null;
  }

  /**
   * Check all feature configuration partial context conditions against passed ast
   *
   * @param ast
   */
  @Override
  public void runDefaultCoCos(ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of a partial FC "abc.BasicPhone.fc" and the output
   * path "target" will be: "target/abc/BasicPhone.fcsym"
   *
   * @return
   */
  public String storeSymbols(IFeatureConfigurationPartialArtifactScope scope, Path out) {
    Path f = out
      .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
      .resolve(scope.getName() + ".fcsym");
    String serialized = new FeatureConfigurationPartialSymbols2Json().serialize(scope);
    FileReaderWriter.storeInFile(f, serialized);
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
  public ASTFeatureConfiguration run(String modelFile, MCPath mp) {

    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);

    // setup the symbol table
    createSymbolTable(ast, mp);

    // check context conditions
    runDefaultCoCos(ast);

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
  public ASTFeatureConfiguration run(String modelFile) {
    // parse the model and create the AST representation
    final ASTFCCompilationUnit ast = parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    MCPath mp = new MCPath(path, FeatureDiagramTool.SYMBOL_OUT);
    createSymbolTable(ast, mp);

    // check context conditions
    runDefaultCoCos(ast);

    // do not store artifact scope

    return ast.getFeatureConfiguration();
  }

  /**
   * This method realizes a tool for processing FC models.
   * See the project's Readme for a documentation of the tool
   *
   * @param args
   */
  public void run(String[] args) {
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar MCFeatureConfigurationPartial.jar", options, true);
        return;
      }

      //Set input file and parse it
      if (!cmd.hasOption("input")) {
        Log.error(
          "0xFE102 The input file is a mandatory argument of the FeatureConfigurationPartialTool!");
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

      // parse, create symbol table, check all cocos
      ASTFCCompilationUnit ast = parse(input);
      IFeatureConfigurationPartialArtifactScope symbolTable = createSymbolTable(ast, mp);
      runDefaultCoCos(ast);

      // print or store symbol table
      if (cmd.hasOption("symboltable")) {
        String s = cmd.getOptionValue("symboltable");
        if (null != s) {
          // store symbol table to passed file
          JsonPrinter.disableIndentation();
          String symbolFile = output.resolve(s).toString();
          storeSymbols(symbolTable, symbolFile);
        }
        else {
          //print (formatted!) symboltable to console
          JsonPrinter.enableIndentation();
          System.out.println(new FeatureConfigurationPartialSymbols2Json().serialize(symbolTable));
        }
      }

      // pretty print  model and either output on stdout or store to file
      if (cmd.hasOption("prettyprint")) {
        this.prettyPrint(ast, resolvePath(output, cmd.getOptionValue("prettyprint")));
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar MCFeatureConfigurationPartial.jar", options, true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

  public Path resolvePath(Path outputPath, String file){
    // outputPath = Paths.get("target")
    if (file == null) return null;
    return outputPath.resolve(file);
  }

  public void prettyPrint (de.monticore.featureconfiguration._ast.ASTFCCompilationUnit ast, Path targetPath) {
    de.monticore.featureconfigurationpartial._prettyprint.FeatureConfigurationPartialFullPrettyPrinter prettyPrinter = new de.monticore.featureconfigurationpartial._prettyprint.FeatureConfigurationPartialFullPrettyPrinter(new de.monticore.prettyprint.IndentPrinter());
    print(prettyPrinter.prettyprint(ast), targetPath);
  }

  public void print(String content, Path targetPath) {
    // print to stdout or file
    if (targetPath == null) {
      System.out.println(content);
    } else {
      de.monticore.io.FileReaderWriter.storeInFile(targetPath, content);
    }
  }

  @Override
  public Options addStandardOptions(Options options) {
    //help
    options.addOption(Option.builder("h")
      .longOpt("help")
      .desc("Prints this help dialog")
      .build());

//parse input file
    options.addOption(Option.builder("i")
      .longOpt("input")
      .argName("file")
      .hasArg()
      .desc("Reads the source file (mandatory) and parses the contents")
      .build());

//pretty print runner
    options.addOption(Option.builder("pp")
      .longOpt("prettyprint")
      .argName("file")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Prints the AST to stdout or the specified file (optional)")
      .build());

// pretty print SC
    options.addOption(Option.builder("s")
      .longOpt("symboltable")
      .argName("file")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Serialized the Symbol table of the given artifact.")
      .build());

//reports about the runner
    options.addOption(Option.builder("r")
      .longOpt("report")
      .argName("dir")
      .hasArg(true)
      .desc("Prints reports of the artifact to the specified directory.")
      .build());

// model paths
    options.addOption(Option.builder("path")
      .hasArgs()
      .desc("Sets the artifact path for imported symbols, space separated.")
      .build());
    return options;
  }

  /**
   * initializes the additional tool options
   */
  @Override
  public Options addAdditionalOptions(Options options) {
    options.addOption("o", "output", true, "Path of generated files");
    return options;
  }
}
