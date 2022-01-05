/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbols2Json;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.featurediagram.prettyprint.FeatureDiagramPrettyPrinter;
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

public class FeatureDiagramTool extends FeatureDiagramToolTOP {

  /**
   * constant for the default folder into which symbols are stored
   */
  public static final Path SYMBOL_OUT = Paths.get("target/symbols");

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @return
   */
  @Override
  public IFeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast) {
    initGlobalScope();
    return super.createSymbolTable(ast);
  }

  public void initGlobalScope() {
    IFeatureDiagramGlobalScope gs = FeatureDiagramMill.globalScope();
    if (null == gs.getFileExt() || gs.getFileExt().isEmpty()) {
      gs.setFileExt("fd");
    }
  }

  /**
   * Check all feature diagram context conditions against the ast passed as argument
   *
   * @param ast
   */
  @Override
  public void runDefaultCoCos(ASTFDCompilationUnit ast) {
    FeatureDiagramCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of an FD "abc.Phone.fd" and the output
   * path "target" will be: "target/abc/Phone.fdsym"
   *
   * @return
   */
  public String storeSymbols(IFeatureDiagramArtifactScope scope, Path out) {
    Path f = out
      .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
      .resolve(scope.getName() + ".fdsym");
    String serialized = new FeatureDiagramSymbols2Json().serialize(scope);
    FileReaderWriter.storeInFile(f, serialized);
    return serialized;
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @return
   */
  public ASTFeatureDiagram run(String modelFile, Path out) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast);

    // execute default context conditions
    runDefaultCoCos(ast);

    // store artifact scope after context conditions have been checked
    storeSymbols(modelTopScope, out);

    return ast.getFeatureDiagram();
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @return
   */
  public ASTFeatureDiagram run(String modelFile) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }
    ModelPaths.addEntry(FeatureDiagramMill.globalScope().getSymbolPath(), path);

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast);

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    storeSymbols(modelTopScope, SYMBOL_OUT);

    return ast.getFeatureDiagram();
  }

  /**
   * This method realizes a tool for processing FC models.
   * See the project's Readme for a documentation of the tool
   *
   * @param args
   */
  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
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
      MCPath mp = FeatureDiagramMill.globalScope().getSymbolPath();
      if (cmd.hasOption("path")) {
        for (String p : cmd.getOptionValues("path")) {
          ModelPaths.addEntry(mp, p);
        }
      }

      //Set output path for stored symbols and pretty printed models (or use default)
      Path output = Paths.get("target");
      if (cmd.hasOption("output")) {
        output = Paths.get(cmd.getOptionValue("output"));
      }

      // parse, create symbol table, check all cocos
      ASTFDCompilationUnit ast = parse(input);
      IFeatureDiagramArtifactScope symbolTable = createSymbolTable(ast);
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
          FeatureDiagramSymbols2Json s2j = new FeatureDiagramSymbols2Json();
          JsonPrinter.enableIndentation();
          System.out.println(s2j.serialize(symbolTable));
        }
      }

      // pretty print  model and either output on stdout or store to file
      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureDiagramPrettyPrinter.print(ast);
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
      formatter.printHelp("java -jar FeatureDiagramTool.jar", options, true);
      Log.error("0xFD114 An exception occured while processing the CLI input!", e);
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
   *
   * @param options the additional tool options for this class
   * @return
   */
  @Override
  public Options addAdditionalOptions(Options options) {
    options.addOption("o", "output", true, "Path of generated files");
    return options;
  }
}
