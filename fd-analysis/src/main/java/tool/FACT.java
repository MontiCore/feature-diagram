/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import tool.analyses.IsValid;

import java.util.Optional;

public class FACT {

  public static final String ARG_IS_VALID = "isValid";

  public static final String ARG_HELP = "help";

  protected CommandLine cmd;

  protected FeatureModelAnalysisTool tool;

  protected Optional<IsValid> isValid = Optional.empty();

  public static void main(String[] args) {
    Log.initWARN();
    new FACT(args);
  }

  public FACT(String[] args) {
    try {
      CommandLineParser parser = new BasicParser();
      cmd = parser.parse(getOptions(), args);
      if(processArguments()){
        tool.performAnalyses();
        printResults();
      }
    }
    catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }

  public static Options getOptions() {
    Options options = new Options();

    createAnalysisOption(options, ARG_IS_VALID, "test.fc", "check if <test.fc> is a valid configuration in the passed feature diagram");
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
    //TODO add other analyses here
    return true;
  }

  protected static void createAnalysisOption(Options options, String key, String argName, String description) {
    Option opt = new Option(key, true, description);
    opt.setArgName(argName);
    options.addOption(opt);
  }

  protected void printResults() {
    if (isValid.isPresent()) {
      Optional<Boolean> result = isValid.get().getResult();
      if (!result.isPresent() || null == result.get()) {
        Log.error("0xFC919 There was an error conducting the 'isValid' analysis!");
      }
      Log.warn("Result of isValid: " + result.get());
    }
  }

  protected ASTFeatureDiagram processFDArgument() {
    if (0 == cmd.getArgList().size()) {
      Log.error("0xFC900 Performing an analysis requires to add a feature diagram as argument!");
      return null;
    }
    else if (1 == cmd.getArgList().size()) {
      ASTFeatureDiagram ast = FeatureDiagramTool.run(cmd.getArgList().get(0).toString());
      tool = new FeatureModelAnalysisTool(ast.getSymbol());
      return ast;
    }
    else {
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
