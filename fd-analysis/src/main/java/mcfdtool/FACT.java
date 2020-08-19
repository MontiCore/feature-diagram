/* (c) https://github.com/MontiCore/monticore */
package mcfdtool;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramScopeDeSer;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IGlobalScope;
import de.se_rwth.commons.logging.Log;
import mcfdtool.analyses.*;
import mcfdtool.transform.flatzinc.Constraint;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;
import org.chocosolver.solver.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FACT is the main class of the Feature Diagram Language
 * <p>
 * It provides a CLI processing inputs and commands to
 * perform analyses on FDs, provide symtabs and pretty printing
 */
public class FACT {

  /**
   * Main function: Delegates to the FACT instance it creates
   *
   * @param args command line arguments (e.g. --help)
   */
  public static void main(String[] args) {
    Log.initWARN();
    new FACT(args);
  }

  /**
   * Uses Apache CLI Parser to access args
   *
   * @param args command line arguments (e.g. --help)
   */
  public FACT(String[] args) {
    try {
      // Basic processing
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(getOptions(), args);

      // Language tool specific handling of commands
      // help:
      if (null == cmd || 0 == cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FACT.jar <test.fd> [analysis options] ", getOptions());
        return;
      }

      // First argument: FD that is being processed
      ASTFeatureDiagram fd = readFeatureDiagram(cmd);

      // check all implemented analysis one after another
      if (cmd.hasOption("isValid")) {
        String fcString = cmd.getOptionValue("isValid"); //read FC file path from command line
        ASTFeatureConfiguration fc = readFeatureConfiguration(fcString, cmd);
        execIsValid(fd, fc);
      }
      if (cmd.hasOption("allProducts")) {
        execAllProducts(fd);
      }
      if (cmd.hasOption("deadFeatures")) {
        execDeadFeature(fd);
      }
      if (cmd.hasOption("falseOptional")) {
        execFalseOptional(fd);
      }
      if (cmd.hasOption("completeToValid")) {
        String fcString = cmd.getOptionValue("completeToValid"); //read FC file path from command line
        ASTFeatureConfiguration fc = readFeatureConfiguration(fcString, cmd);
        execCompleteToValid(fd, fc);
      }
      if (cmd.hasOption("findValid")) {
        execFindValid(fd);
      }
      if (cmd.hasOption("isVoidFeatureModel")) {
        execIsVoidFeatureModel(fd);
      }
      if (cmd.hasOption("numberOfProducts")) {
        execNumberOfProducts(fd);
      }
    }
    catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }

  /**
   * handle -allProducts
   */
  public static List<ASTFeatureConfiguration> execAllProducts(ASTFeatureDiagram fd) {
    AllProducts analysis = new AllProducts();
    List<ASTFeatureConfiguration> result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC774 AllProducts was not successful");
    }
    else {
      System.out.println("Result of AllProducts: " + FeatureConfigurationPartialPrettyPrinter.print(result));
    }
    return result;
  }

  /**
   * handle -completeToValid
   */
  public static ASTFeatureConfiguration execCompleteToValid(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    CompleteToValid analysis = new CompleteToValid();
    ASTFeatureConfiguration result = analysis.perform(fd, fc);

    if (null == result) {
      Log.error("0xFC775 CompleteToValid was not successful");
    }
    else {
      System.out.println("Result of CompleteToValid: " + FeatureConfigurationPartialPrettyPrinter.print(result));
    }
    return result;
  }

  /**
   * handle -deadFeature
   */
  public static List<String> execDeadFeature(ASTFeatureDiagram fd) {
    DeadFeature analysis = new DeadFeature();
    List<String> result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC776 DeadFeature was not successful");
    }
    else {
      System.out.println("Result of DeadFeature: " + result.stream().collect(Collectors.joining(", ")));
    }
    return result;
  }

  /**
   * handle -falseOptional
   */
  public static List<String> execFalseOptional(ASTFeatureDiagram fd) {
    FalseOptional analysis = new FalseOptional();
    List<String> result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC777 FalseOptional was not successful");
    }
    else {
      System.out.println("Result of FalseOptional: " + result.stream().collect(Collectors.joining(", ")));
    }
    return result;
  }

  /**
   * handle -findValid
   */
  public static ASTFeatureConfiguration execFindValid(ASTFeatureDiagram fd) {
    FindValid analysis = new FindValid();
    ASTFeatureConfiguration result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC774 FindValid was not successful");
    }
    else {
      System.out.println("Result of FindValid: " + FeatureConfigurationPartialPrettyPrinter.print(result));
    }
    return result;
  }

  /**
   * handle -generalFilter
   */
  public static List<ASTFeatureConfiguration> execGeneralFilter(ASTFeatureDiagram fd, List<Constraint> constraints) {
    GeneralFilter analysis = new GeneralFilter();
    List<ASTFeatureConfiguration> result = analysis.perform(fd, constraints);

    if (null == result) {
      Log.error("0xFC778 GeneralFilter was not successful");
    }
    else {
      System.out.println("Result of GeneralFilter: " + FeatureConfigurationPartialPrettyPrinter.print(result));
    }
    return result;
  }

  /**
   * handle -isValid
   */
  public static boolean execIsValid(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    IsValid analysis = new IsValid();
    Boolean result = analysis.perform(fd, fc);

    if (null == result) {
      Log.error("0xFC779 IsValid was not successful");
    }
    else {
      System.out.println("Result of IsValid: " + result);
    }
    return result;
  }

  /**
   * handle -isVoidFeatureModel
   */
  public static boolean execIsVoidFeatureModel(ASTFeatureDiagram fd) {
    IsVoidFeatureModel analysis = new IsVoidFeatureModel();
    Boolean result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC780 IsVoidFeatureModel was not successful");
    }
    else {
      System.out.println("Result of IsVoidFeatureModel: " + result);
    }
    return result;
  }

  /**
   * handle -numberOfProducts
   */
  public static int execNumberOfProducts(ASTFeatureDiagram fd) {
    NumberOfProducts analysis = new NumberOfProducts();
    Integer result = analysis.perform(fd);

    if (null == result) {
      Log.error("0xFC781 NumberOfProducts was not successful");
    }
    else {
      System.out.println("Result of NumberOfProducts: " + result);
    }
    return result;
  }

  /**
   * Read in a Feature Diagram at the passed location, with the passed location for storing FD symbol
   * tables and the passed ModelPath for searching stored symbol tables (e.g., of imported FDs)
   * @param modelFile
   * @param symbolOutPath
   * @param symbolInputPath
   * @return
   */
  public static ASTFeatureDiagram readFeatureDiagram(String modelFile, String symbolOutPath, ModelPath symbolInputPath){
    return FeatureDiagramTool.run(modelFile, Paths.get(symbolOutPath), symbolInputPath);
  }

  /**
   * Read in a Feature Configuration at the passed location with the passed  ModelPath for searching
   * stored symbol tables (of FDs). No symbol table is stored for FCs themselves.
   * @param modelFile
   * @param symbolInputPath
   * @return
   */
  public static ASTFeatureConfiguration readFeatureConfiguration(String modelFile, ModelPath symbolInputPath){
    return FeatureConfigurationTool.run(modelFile, symbolInputPath);
  }

  /**
   * Read in a Feature Configuration at the passed location. Use the base location of the passed FC
   * (= path without FC file name and FC package) for searching for stored symbol tables (of FDs).
   * No symbol table is stored for FCs themselves.
   * @param modelFile
   * @return
   */
  public static ASTFeatureConfiguration readFeatureConfiguration(String modelFile){
    return FeatureConfigurationTool.run(modelFile);
  }

  /**
   * read a feature diagram from the passed command line.
   * The feature diagram is the only "argument" in the command line, as all other
   * arguments should be parsed as "options" and are treated elsewhere. Stores the symbols
   * at a specified location if the option "symbolPath" is set
   *
   * @param cmd
   * @return
   */
  protected ASTFeatureDiagram readFeatureDiagram(CommandLine cmd) {
    if (0 == cmd.getArgList().size()) {
      Log.error("0xFC900 No feature diagram given as first argument!");
      return null;
    }
    else if (1 == cmd.getArgList().size()) {
      String fdModelFile = cmd.getArgList().get(0).toString();

      //by default, use this for the symbol output
      String symbolOutPath = FeatureDiagramTool.SYMBOL_LOCATION.toString();

      //except if the option "symbolPath" is set, then use the passed location to store (and load) symbols
      if(cmd.hasOption("symbolPath")){
        symbolOutPath = cmd.getOptionValue("symbolPath");
      }

      return readFeatureDiagram(fdModelFile, symbolOutPath, new ModelPath(Paths.get(symbolOutPath)));
    }
    else {
      for (int i = 1; i < cmd.getArgList().size(); i++) {
        Log.error("0xFC999 Unknown arguments '" + cmd.getArgList().get(i) + "'");
      }
      return null;
    }
  }

  /**
   * Read a feature configuration from the passed command line.
   * @param fcString
   * @param cmd
   * @return
   */
  protected ASTFeatureConfiguration readFeatureConfiguration(String fcString, CommandLine cmd) {
    //Without setting a modelpath, the FC tool would never be able to search
    // for the FD model referenced in the FC. Therefore, identifying this is mandatory here.

    //if the option "modelPath" is set, use the passed location to load symbols
    if(cmd.hasOption("modelPath")) {
      ModelPath mp = new ModelPath(Paths.get(cmd.getOptionValue("modelPath")));
      return readFeatureConfiguration(fcString, mp);
    }
    //if "modelPath" is not set, but "symbolPath" is set to store FD symbols, use this as modelPath
    else if(cmd.hasOption("symbolPath")){
      ModelPath mp = new ModelPath(Paths.get(cmd.getOptionValue("symbolPath")));
      return readFeatureConfiguration(fcString, mp);
    }
    // else use the folder, in which the FC model is located, as modelpath. This is done by the
    // invoked method below
    else{
      return readFeatureConfiguration(fcString);
    }
  }

  /**
   * This creates the options for the FACT tool. These are both used for the CommandLineParser
   * and printing the "help" option.
   * @return
   */
  protected static Options getOptions() {
    Options options = new Options();

    createAnalysisOption(options, "isValid", "test.fc",
        "check if <test.fc> is a valid configuration in the passed feature diagram");
    createAnalysisOption(options, "allProducts", null,
        "find all valid configurations for the passed feature diagram");
    createAnalysisOption(options, "deadFeatures", null, "find all dead " +
        "features for the passed feature diagram");
    createAnalysisOption(options, "falseOptional", null, "find " +
        "all false optional features for the pass feature diagram");
    createAnalysisOption(options, "completeToValid", "test.fc", "find a valid " +
        "configurations that fulfils the partial configuration <test.fc>");
    createAnalysisOption(options, "findValid", null, "find a valid" +
        " configuration for the passed feature model");
    createAnalysisOption(options, "isVoidFeatureModel", null, "check if the passed " +
        "feature model has any valid configuration");
    createAnalysisOption(options, "numberOfProducts", null, "calculate " +
        "the number of valid configurations for the passed feature model");
    // ... place for further analyses

    options.addOption("h", "help", false, "show this explanation");
    return options;
  }

  protected static void createAnalysisOption(Options options, String key, String argName,
      String description) {
    Option opt = new Option(key, argName != null, description);
    if (argName != null) {
      opt.setArgName(argName);
    }
    options.addOption(opt);
  }

}
