/* (c) https://github.com/MontiCore/monticore */
package mcfdtool;

import de.monticore.featureconfiguration.FeatureConfigurationCLI;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationScopeDeSer;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramCLI;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramScopeDeSer;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import fddiff.FDSemDiff;
import freemarker.core.OptInTemplateClassResolver;
import mcfdtool.analyses.*;
import mcfdtool.transform.flatzinc.Constraint;
import org.apache.commons.cli.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FACT is the main class of the Feature Diagram Language
 * <p>
 * It provides a CLI processing inputs and commands to
 * perform analyses on FDs, provide symtabs and pretty printing
 */
public class FACT {

  protected FeatureDiagramCLI fdTool = new FeatureDiagramCLI();

  protected FeatureDiagramParser fdParser = new FeatureDiagramParser();

  protected FeatureDiagramScopeDeSer fdDeSer = new FeatureDiagramScopeDeSer();

  protected FeatureConfigurationCLI fcTool = new FeatureConfigurationCLI();

  protected FeatureConfigurationParser fcParser = new FeatureConfigurationParser();

  protected FeatureConfigurationScopeDeSer fcDeSer = new FeatureConfigurationScopeDeSer();

  /**
   * Main function: Delegates to the FACT instance it creates
   *
   * @param args command line arguments (e.g. --help)
   */
  public static void main(String[] args) {
    FACT tool = new FACT();
    Log.initWARN();
    tool.run(args);
  }

  /**
   * Uses Apache CLI Parser to access args
   *
   * @param args command line arguments (e.g. --help)
   */
  public void run(String[] args) {

    //init CLI options and log
    Options options = initOptions();
    try {
      // Basic processing
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(options, args);

      // Language tool specific handling of commands
      // help:
      if (null == cmd || 0 == cmd.getArgList().size() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar FACT.jar <test1.fd> <test2.fd>? [analysis options] ", options);
        return;
      }

      // First argument: First FD that is being processed
      ASTFeatureDiagram firstFD = readFeatureDiagram(cmd, 0);

      // check all implemented analysis one after another
      if (cmd.hasOption("isValid")) {
        String fcString = cmd.getOptionValue("isValid"); //read FC file path from command line
        ASTFeatureConfiguration fc = readFeatureConfiguration(fcString, cmd);
        execIsValid(firstFD, fc);
      }
      if (cmd.hasOption("allProducts")) {
        execAllProducts(firstFD);
      }
      if (cmd.hasOption("deadFeatures")) {
        execDeadFeature(firstFD);
      }
      if (cmd.hasOption("falseOptional")) {
        execFalseOptional(firstFD);
      }
      if (cmd.hasOption("completeToValid")) {
        String fcString = cmd.getOptionValue("completeToValid"); //read FC file path from command line
        ASTFeatureConfiguration fc = readFeatureConfiguration(fcString, cmd);
        execCompleteToValid(firstFD, fc);
      }
      if (cmd.hasOption("findValid")) {
        execFindValid(firstFD);
      }
      if (cmd.hasOption("isVoidFeatureModel")) {
        execIsVoidFeatureModel(firstFD);
      }
      if (cmd.hasOption("numberOfProducts")) {
        execNumberOfProducts(firstFD);
      }
      if (cmd.hasOption("semdiff") && cmd.getArgList().size() == 2) {
        ASTFeatureDiagram secondFD = readFeatureDiagram(cmd, 1);
        if(secondFD != null) {
          String optionVal = cmd.getOptionValue("semdiff");
          execSemDiff(firstFD, secondFD, optionVal);
        }
      }
    }
    catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }

  private void execSemDiff(ASTFeatureDiagram from, ASTFeatureDiagram to, String optionVal) {
    FDSemDiff fdSemDiff = new FDSemDiff();
    Optional<ASTFeatureConfiguration> witness = Optional.empty();

    if(optionVal.equals("open") || optionVal.equals("closed")) {
      if (optionVal == null || optionVal.equals("open")) {
        witness = fdSemDiff.semDiffOpenWorld(from, to);
      }
      else if (optionVal.equals("closed")) {
        witness = fdSemDiff.semDiffClosedWorld(from, to);
      }
      if(witness.isPresent()) {
        System.out.println("Diff witness: " + FeatureConfigurationPartialPrettyPrinter.print(witness.get()));
      }
      else {
        System.out.println("The first input FD is a refinement of the second input FD.");
      }
    }
    else {
      Log.error(String.format("0xFC902 Unknown value '%s' for the argument 'semantics' of the option '-semdiff'. "
        + "Possible values for the argument are 'open' and 'closed'.", optionVal));
    }
  }

  /**
   * handle -allProducts
   */
  public List<ASTFeatureConfiguration> execAllProducts(ASTFeatureDiagram fd) {
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
  public ASTFeatureConfiguration execCompleteToValid(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
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
  public List<String> execDeadFeature(ASTFeatureDiagram fd) {
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
  public List<String> execFalseOptional(ASTFeatureDiagram fd) {
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
  public ASTFeatureConfiguration execFindValid(ASTFeatureDiagram fd) {
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
  public List<ASTFeatureConfiguration> execGeneralFilter(ASTFeatureDiagram fd, List<Constraint> constraints) {
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
  public boolean execIsValid(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
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
  public boolean execIsVoidFeatureModel(ASTFeatureDiagram fd) {
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
  public int execNumberOfProducts(ASTFeatureDiagram fd) {
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
   *
   * @param modelFile
   * @param symbolOutPath
   * @param symbolInputPath
   * @return
   */
  public ASTFeatureDiagram readFeatureDiagram(String modelFile, String symbolOutPath, ModelPath symbolInputPath) {
    return fdTool.run(modelFile, Paths.get(symbolOutPath), symbolInputPath, fdParser, fdDeSer);
  }

  /**
   * Read in a Feature Configuration at the passed location with the passed  ModelPath for searching
   * stored symbol tables (of FDs). No symbol table is stored for FCs themselves.
   *
   * @param modelFile
   * @param symbolInputPath
   * @return
   */
  public ASTFeatureConfiguration readFeatureConfiguration(String modelFile, ModelPath symbolInputPath) {
    return fcTool.run(modelFile, symbolInputPath, fcParser, fcDeSer);
  }

  /**
   * Read in a Feature Configuration at the passed location. Use the base location of the passed FC
   * (= path without FC file name and FC package) for searching for stored symbol tables (of FDs).
   * No symbol table is stored for FCs themselves.
   *
   * @param modelFile
   * @return
   */
  public ASTFeatureConfiguration readFeatureConfiguration(String modelFile) {
    return fcTool.run(modelFile, fcParser, fcDeSer);
  }

  /**
   * reads the feature diagram passed as the num-th argument (starting at 0) to the command line.
   * The feature diagrams are the only "arguments" in the command line, as all other
   * arguments should be parsed as "options" and are treated elsewhere. Stores the symbols
   * at a specified location if the option "symbolPath" is set
   *
   * @param cmd
   * @param num
   * @return
   */
  protected ASTFeatureDiagram readFeatureDiagram(CommandLine cmd, int num) {
    if (0 == cmd.getArgList().size()) {
      Log.error("0xFC900 No feature diagram given as first argument!");
      return null;
    }
    else if (cmd.getArgList().size() <= 2) {
      if (cmd.getArgList().size() < num + 1) {
        // received a smaller number of FDs as inputs as expected
        Log.error(String.format("0xFC998 Received %s feature diagrams as inputs. "
          + "Expecting at least %s feature diagrams as inputs!", cmd.getArgList().size(), num + 1));
        return null;
      }
      else {
        String fdModelFile = cmd.getArgList().get(num).toString();

        //by default, use this for the symbol output
        String symbolOutPath = FeatureDiagramCLI.SYMBOL_OUT.toString();

        //except if the option "symbolPath" is set, then use the passed location to store (and load) symbols
        if (cmd.hasOption("symbolPath")) {
          symbolOutPath = cmd.getOptionValue("symbolPath");
        }
        ModelPath mp = new ModelPath(Paths.get(symbolOutPath));
        return readFeatureDiagram(fdModelFile, symbolOutPath, mp);
      }
    }
    else {
      // received a greater number of arguments as inputs as expected (max 2 for semdiff)
      for (int i = 0; i < cmd.getArgList().size(); i++) {
        Log.error("0xFC999 Unknown arguments '" + cmd.getArgList().get(i) + "'");
      }
      return null;
    }
  }

  /**
   * Read a feature configuration from the passed command line.
   *
   * @param fcString
   * @param cmd
   * @return
   */
  protected ASTFeatureConfiguration readFeatureConfiguration(String fcString, CommandLine cmd) {
    //Without setting a modelpath, the FC tool would never be able to search
    // for the FD model referenced in the FC. Therefore, identifying this is mandatory here.

    //if the option "modelPath" is set, use the passed location to load symbols
    if (cmd.hasOption("modelPath")) {
      ModelPath mp = new ModelPath(Paths.get(cmd.getOptionValue("modelPath")));
      return readFeatureConfiguration(fcString, mp);
    }
    //if "modelPath" is not set, but "symbolPath" is set to store FD symbols, use this as modelPath
    else if (cmd.hasOption("symbolPath")) {
      ModelPath mp = new ModelPath(Paths.get(cmd.getOptionValue("symbolPath")));
      return readFeatureConfiguration(fcString, mp);
    }
    // else use the folder, in which the FC model is located, as modelpath. This is done by the
    // invoked method below
    else {
      return readFeatureConfiguration(fcString);
    }
  }

  /**
   * This creates the options for the FACT tool. These are both used for the CommandLineParser
   * and printing the "help" option.
   *
   * @return
   */
  protected Options initOptions() {
    Options options = new Options();

    createAnalysisOption(options, "isValid", "test.fc", false,"check if <test.fc> is a valid configuration in <test1.fd>");
    createAnalysisOption(options, "allProducts", null,false, "find all valid configurations for <test1.fd>");
    createAnalysisOption(options, "deadFeatures", null, false,"find all dead "
      + "features for <test1.fd>");
    createAnalysisOption(options, "falseOptional", null, false,"find "
      + "all false optional features for <test1.fd>");
    createAnalysisOption(options, "completeToValid", "test.fc", false,"find a valid "
      + "configurations of <test1.fd> that fulfils the partial configuration <test.fc>");
    createAnalysisOption(options, "findValid", null, false,"find a valid"
      + " configuration for <test1.fd>");
    createAnalysisOption(options, "isVoidFeatureModel", null, false,"check if <test1.fd> "
      + "has any valid configuration");
    createAnalysisOption(options, "numberOfProducts", null,false, "calculate "
      + "the number of valid configurations for <test1.fd>");
    createAnalysisOption(options, "semdiff", "semantics", true,"calculate "
      + "a diff witness contained in the semantic difference from <test1.fd> to <test2.fd> using the semantics "
      + "as specified by the argument <semantics>. Possible values for the argument are 'closed' and 'open' "
      + "for choosing between the closed- and open-world semantics. If no argument is specified, then 'open' "
      + "is chosen by default. The differences between the semantics are described here: "
      + "'https://se-rwth.de/publications/Semantic-Evolution-Analysis-of-Feature-Models.pdf'");
    // ... place for further analyses

    options.addOption("h", "help", false, "show this explanation");
    return options;
  }

  protected static void createAnalysisOption(Options options, String key, String argName, boolean optionalArg, String description) {
    Option opt = new Option(key, argName != null, description);
    if (argName != null) {
      opt.setArgName(argName);
      opt.setOptionalArg(optionalArg);
    }
    options.addOption(opt);
  }

}
