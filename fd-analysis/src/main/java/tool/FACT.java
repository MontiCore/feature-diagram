/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import tool.analyses.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FACT {

  public static final String ARG_IS_VALID = "isValid";
  public static final String ARG_ALL_PRODUCTS = "allProducts";
  public static final String ARG_DEAD_FEATURES = "dead";
  public static final String ARG_FALSE_OPTIONAL_FEATURES = "falseOpt";
  public static final String ARG_FIND_VALID_PRODUCT = "findValid";
  public static final String ARG_FILTER = "filter";
  public static final String ARG_IS_VOID = "isVoid";
  public static final String ARG_NUMBER_OF_PRODUCTS = "numProducts";

  public static final String ARG_HELP = "help";

  protected CommandLine cmd;

  protected FeatureModelAnalysisTool tool;

  protected Optional<IsValid> isValid = Optional.empty();
  protected Optional<AllProducts> allProducts = Optional.empty();
  protected Optional<DeadFeature> dead = Optional.empty();
  protected Optional<FalseOptional> falseOpt = Optional.empty();
  protected Optional<FindValidConfig> findValid = Optional.empty();
  protected Optional<Filter> filter = Optional.empty();
  protected Optional<IsVoidFeatureModel> isVoid = Optional.empty();
  protected Optional<NumberOfProducts> numProducts = Optional.empty();


  public static void main(String[] args) {
    Log.initWARN();
    new FACT(args);
  }

  public FACT(String[] args) {
    try {
      CommandLineParser parser = new BasicParser();
      cmd = parser.parse(getOptions(), args);
      if (processArguments()) {
        tool.performAnalyses();
        printResults();
      }
    } catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }

  public static Options getOptions() {
    Options options = new Options();

    createAnalysisOption(options, ARG_IS_VALID, "test.fc",
            "check if <test.fc> is a valid configuration in the passed feature diagram");
    createAnalysisOption(options, ARG_ALL_PRODUCTS, null,
            "find all valid configurations for the passed feature diagram");
    createAnalysisOption(options, ARG_DEAD_FEATURES, null, "find all dead " +
            "features for the passed feature diagram");
    createAnalysisOption(options, ARG_FALSE_OPTIONAL_FEATURES, null, "find " +
            "all false optional features for the pass feature diagram");
    createAnalysisOption(options, ARG_FILTER, "test.fc", "find all valid " +
            "configurations that fulfill the partial configuration <test.fc>");
    createAnalysisOption(options, ARG_FIND_VALID_PRODUCT, null, "find a valid" +
            " configuration for the passed feature model");
    createAnalysisOption(options, ARG_IS_VOID, null, "check if the passed " +
            "feature model has any valid configuration");
    createAnalysisOption(options, ARG_NUMBER_OF_PRODUCTS, null, "calculate " +
            "the number of valid configurations for the passed feature model");
    //TODO add other analyses here

    options.addOption("h", ARG_HELP, false, "show this explanation");
    return options;
  }

  protected boolean processArguments() {
    if (null == cmd || cmd.hasOption(ARG_HELP)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FACT.jar <test.fd> [analysis options] ", getOptions());
      return false;
    }
    ASTFeatureDiagram fd = processFDArgument();

    if (cmd.hasOption(ARG_IS_VALID)) {
      ASTFeatureConfiguration fc = processFCArgument("IsValid", ARG_IS_VALID, fd.getName());
      isValid = Optional.of(new IsValid(fc));
      tool.addAnalysis(isValid.get());
    }
    if (cmd.hasOption(ARG_ALL_PRODUCTS)) {
      allProducts = Optional.of(new AllProducts());
      tool.addAnalysis(allProducts.get());
    }
    if (cmd.hasOption(ARG_DEAD_FEATURES)) {
      dead = Optional.of(new DeadFeature());
      tool.addAnalysis(dead.get());
    }
    if (cmd.hasOption(ARG_FALSE_OPTIONAL_FEATURES)) {
      falseOpt = Optional.of(new FalseOptional());
      tool.addAnalysis(falseOpt.get());
    }
    if (cmd.hasOption(ARG_FILTER)) {
      ASTFeatureConfiguration fc = processFCArgument("Filter", ARG_FILTER,
              fd.getName());
      filter = Optional.of(new Filter(fc));
      tool.addAnalysis(filter.get());
    }
    if (cmd.hasOption(ARG_FIND_VALID_PRODUCT)) {
      findValid = Optional.of(new FindValidConfig());
      tool.addAnalysis(findValid.get());
    }
    if (cmd.hasOption(ARG_IS_VOID)) {
      isVoid = Optional.of(new IsVoidFeatureModel());
      tool.addAnalysis(isVoid.get());
    }
    if (cmd.hasOption(ARG_NUMBER_OF_PRODUCTS)) {
      numProducts = Optional.of(new NumberOfProducts());
      tool.addAnalysis(numProducts.get());
    }
    //TODO add other analyses here
    return true;
  }

  protected static void createAnalysisOption(Options options, String key, String argName,
                                             String description) {
    Option opt = new Option(key, argName != null, description);
    if (argName != null){opt.setArgName(argName);}
    options.addOption(opt);
  }

  protected void printResults() {
    if (isValid.isPresent()) {
      Optional<Boolean> result = isValid.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC919 There was an error conducting the 'isValid' analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of isValid: " + result.get());
    }
    if (allProducts.isPresent()) {
      Optional<Set<ASTFeatureConfiguration>> result = allProducts.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC920 There was an error conducting the 'allProducts' " +
          "analysis!");
      }
      System.out.println("Result of allProducts:");
      result.get().forEach(
        config -> {
          System.out.println(FeatureConfigurationPartialPrettyPrinter.print(config));
          System.out.println();
        }
      );
    }
    if (dead.isPresent()) {
      Optional<List<String>> result = dead.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC921 There was an error conducting the 'deadFeatures' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of deadFeatures: " + String.join(",", result.get()));
    }
    if (falseOpt.isPresent()) {
      Optional<List<String>> result = falseOpt.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC922 There was an error conducting the 'falseOptional' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of falseOptional: " + String.join(",",
        result.get()));
    }
    if (filter.isPresent()) {
      Optional<Set<ASTFeatureConfiguration>> result = filter.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC923 There was an error conducting the 'filter' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of filter:");
      result.get().forEach(
        config -> {
          System.out.println(FeatureConfigurationPartialPrettyPrinter.print(config));
          System.out.println();
        }
      );
    }
    if (findValid.isPresent()) {
      Optional<ASTFeatureConfiguration> result = findValid.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC924 There was an error conducting the 'findValid' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of findValid: " + FeatureConfigurationPartialPrettyPrinter.print(result.get()));
    }if (isVoid.isPresent()) {
      Optional<Boolean> result = isVoid.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC925 There was an error conducting the 'isVoid' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of isVoid: " + result.get());
    }
    if (numProducts.isPresent()) {
      Optional<Integer> result = numProducts.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC926 There was an error conducting the 'numProducts' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of numProducts: " + result.get());
    }
    //TODO add other analyses here
  }

  protected ASTFeatureDiagram processFDArgument() {
    if (0 == cmd.getArgList().size()) {
      Log.error("0xFC900 Performing an analysis requires to add a feature diagram as argument!");
      return null;
    } else if (1 == cmd.getArgList().size()) {
      ASTFeatureDiagram ast = FeatureDiagramTool.run(cmd.getArgList().get(0).toString());
      tool = new FeatureModelAnalysisTool(ast);
      return ast;
    } else {
      for (int i = 1; i < cmd.getArgList().size(); i++) {
        Log.error("0xFC999 Unknown argument '" + cmd.getArgList().get(i) + "'");
      }
      return null;
    }
  }

  protected ASTFeatureConfiguration processFCArgument(String analysisName,
                                                      String analysisArg, String fdName) {
    if (cmd.hasOption(analysisArg)) {
      ASTFeatureConfiguration ast = FeatureConfigurationTool.run(cmd.getOptionValue(analysisArg));
      if (!ast.isPresentFdNameSymbol()) {
        //elsewhere, an error is produced already
        return ast;
      }
      if (!ast.getFdNameSymbol().getFullName().equals(fdName)) {
        Log.error(
                "0xFC902 The feature configuration '" + ast.getName() + "' is for the feature diagram '"
                        + ast.getFdNameSymbol().getFullName()
                        + "' that does not match the passed feature diagram `" + fdName + "`!");
      }
      return ast;
    }
    Log.error("0xFC903 Performing an '" + analysisName
            + "' analysis requires to add a feature configuration as argument!");
    return null;
  }

}
