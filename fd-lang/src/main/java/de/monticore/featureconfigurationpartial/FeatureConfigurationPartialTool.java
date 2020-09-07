/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureDiagramResolvingDelegate;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCos;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbolTableCreatorDelegator;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialGlobalScope;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FeatureConfigurationPartialTool {

  /**
   * Check all feature configuration partial context conditions against passed ast
   *
   * @param ast
   */
  public static void checkCoCos(ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialCoCos.checkAll(ast);
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
  public static IFeatureConfigurationPartialArtifactScope createSymbolTable(String model,
      ModelPath mp) {
    return createSymbolTable(mp, parse(model));
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param mp
   * @param ast
   * @return
   */
  public static IFeatureConfigurationPartialArtifactScope createSymbolTable(ModelPath mp,
      ASTFCCompilationUnit ast) {
    FeatureConfigurationPartialSymbolTableCreatorDelegator symbolTable = FeatureConfigurationPartialMill
        .featureConfigurationPartialSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(createGlobalScope(mp))
        .build();
    return symbolTable.createFromAST(ast);
  }

  public static IFeatureConfigurationPartialGlobalScope createGlobalScope(ModelPath mp) {
    return FeatureConfigurationPartialMill
        .featureConfigurationPartialGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fc")
        .addAdaptedFeatureDiagramSymbolResolvingDelegate(new FeatureDiagramResolvingDelegate(mp))
        .build();
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
        formatter.printHelp("java -jar FeatureConfigurationPartialTool.jar", getOptions(), true);
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
        Log.error(
            "0xFD102 The input file is a mandatory argument of the FeatureConfigurationPartialTool!");
      }
      String input = cmd.getOptionValue("input");
      ASTFCCompilationUnit ast = FeatureConfigurationPartialTool.parse(input);

      // create symbol table and check all cocos
      FeatureConfigurationPartialTool.createSymbolTable(mp, ast);
      FeatureConfigurationPartialTool.checkCoCos(ast);

      //No symbol table is stored for partial feature configuration models

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
      formatter.printHelp("java -jar FeatureConfigurationPartialTool.jar", getOptions(), true);
      Log.error("0xFD112 An exception occured while processing the CLI input!", e);
    }
  }

  protected static Options getOptions() {
    Options options = new Options();
    options.addOption("h", "help", false, "Prints this help dialog");
    options.addOption("i", "input", true,
        "Reads the (mandatory) source file resp. the contents of the model");
    options.addOption("o", "output", true, "Path of generated files");
    options.addOption("path", true, "Sets the artifact path for imported symbols");

    Option prettyprint = new Option("pp",
        "Prints the AST to stdout and, if present, the specified output file");
    prettyprint.setOptionalArg(true);
    prettyprint.setLongOpt("prettyprint");
    options.addOption(prettyprint);

    return options;
  }

}
