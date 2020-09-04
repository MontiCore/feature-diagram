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
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;

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
  public static IFeatureDiagramArtifactScope createSymbolTable(String model, ModelPath mp) {
    return createSymbolTable(parse(model), mp);
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @param mp
   * @return
   */
  public static IFeatureDiagramArtifactScope createSymbolTable(ASTFDCompilationUnit ast,
      ModelPath mp) {
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
  public static IFeatureDiagramGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fd")
        .build();
  }

  /**
   * Check all feature diagram context conditions against passed ast
   *
   * @param ast
   */
  public static void checkCoCos(ASTFDCompilationUnit ast) {
    FeatureDiagramCoCos.checkAll(ast);
  }

  /**
   * stores the symbol table of a passed ast in a file at the passed fileName
   *
   * @return
   */
  public static String storeSymbols(IFeatureDiagramArtifactScope scope, Path symbolPath) {
    deser.store(scope, symbolPath);
    return deser.serialize(scope);
  }

  /**
   * stores the symbol table of a passed ast in a file at the passed fileName
   *
   * @return
   */
  public static String storeSymbols(IFeatureDiagramArtifactScope scope, String fileName) {
    File f = new File(fileName);
    String serialized = deser.serialize(scope);
    FileReaderWriter.storeInFile(f.toPath(), serialized);
    return serialized;
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @param mp
   * @return
   */
  public static ASTFeatureDiagram run(String modelFile, Path symbolStoreLocation, ModelPath mp) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast, mp);

    // execute default context conditions
    checkCoCos(ast);

    // store artifact scope after context conditions have been checked
    deser.store(modelTopScope, symbolStoreLocation);

    return ast.getFeatureDiagram();
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table) and stores symbols at default location
   *
   * @param modelFile
   * @param mp
   * @return
   */
  public static ASTFeatureDiagram run(String modelFile, ModelPath mp) {
    return run(modelFile, SYMBOL_LOCATION, mp);
  }

  /**
   * Processes a feature model (parsing, symbol table creation, context condition checking,
   * and storing of symbol table)
   *
   * @param modelFile
   * @return
   */
  public static ASTFeatureDiagram run(String modelFile) {

    // parse the model and create the AST representation
    final ASTFDCompilationUnit ast = parse(modelFile);

    //reconstruct modelpath from input file
    Path path = Paths.get(modelFile).toAbsolutePath().getParent();
    if (ast.isPresentPackage()) {
      for (int i = 0; i < ast.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }

    // setup the symbol table
    IFeatureDiagramArtifactScope modelTopScope = createSymbolTable(ast,
        new ModelPath(path, SYMBOL_LOCATION));

    // execute default context conditions
    FeatureDiagramCoCos.checkAll(ast);

    // store artifact scope after context conditions have been checked
    deser.store(modelTopScope, SYMBOL_LOCATION);

    return ast.getFeatureDiagram();
  }

  /**
   * This main method realizes a CLI for processing FC models.
   * See the project's Readme for a documentation of the CLI
   *
   * @param args
   */
  public static void main(String[] args) {
    Log.initWARN();
    try {
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(getOptions(), args);
      if (null == cmd || 0 != cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FeatureDiagramTool.jar", getOptions(), true);
        return;
      }

      //Set path for imported symbols
      ModelPath mp = new ModelPath();
      if (cmd.hasOption("path")) {
        mp.addEntry(Paths.get(cmd.getOptionValue("path")));
      }

      //Set output path for stored symbols (or use default)
      Path output = Paths.get("target");
      if (cmd.hasOption("output")) {
        output = Paths.get(cmd.getOptionValue("output"));
      }

      //Set input file and parse it
      if (!cmd.hasOption("input")) {
        Log.error("0xFD102 The input file is a mandatory argument of the FeatureDiagramTool!");
      }
      String input = cmd.getOptionValue("input");
      ASTFDCompilationUnit ast = FeatureDiagramTool.parse(input);

      // create symbol table, check all cocos, and store symbol table
      if (cmd.hasOption("symboltable")) {
        IFeatureDiagramArtifactScope symbolTable = FeatureDiagramTool.createSymbolTable(ast, mp);
        FeatureDiagramTool.checkCoCos(ast);

        String s = cmd.getOptionValue("symboltable");
        if(null != s){
          String symbolFile = output.resolve(s).toString();
          System.out.println(FeatureDiagramTool.storeSymbols(symbolTable, symbolFile));
        }
        else{
          System.out.println(FeatureDiagramTool.storeSymbols(symbolTable, output));
        }
      }

      if (cmd.hasOption("prettyprint")) {
        String prettyPrinted = FeatureDiagramPrettyPrinter.print(ast);
        System.out.println(prettyPrinted);
        String outFile = cmd.getOptionValue("prettyprint");
        if(null!=outFile){
          FileReaderWriter.storeInFile(output.resolve(outFile), prettyPrinted);
        }
      }
    }
    catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FeatureDiagramTool.jar", getOptions(), true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

  protected static Options getOptions() {
    Options options = new Options();
    options.addOption("h", "help", false, "Prints this help dialog");
    options.addOption("i", "input", true, "Reads the (mandatory) source file resp. the contents of the model");
    options.addOption("o", "output", true, "Path of generated files");
    options.addOption("path", true, "Sets the artifact path for imported symbols");

    Option symboltable = new Option("s", "Serializes and prints the symbol table to stdout, if present, the specified output file");
    symboltable.setOptionalArg(true);
    symboltable.setLongOpt("symboltable");
    options.addOption(symboltable);

    Option prettyprint = new Option("pp", "Prints the AST to stdout and, if present, the specified output file");
    prettyprint.setOptionalArg(true);
    prettyprint.setLongOpt("prettyprint");
    options.addOption(prettyprint);

    return options;
  }


}
