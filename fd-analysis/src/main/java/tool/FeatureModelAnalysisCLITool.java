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

public class FeatureModelAnalysisCLITool {

  public static final String ARG_FD = "fd";

  public static final String ARG_FC = "fc";

  public static final String ARG_IS_VALID = "isValid";

  public static final String ARG_HELP = "help";

  protected CommandLine cmd;

  protected FeatureModelAnalysisTool tool;

  protected Optional<IsValid> isValid = Optional.empty();

  public static void main(String[] args) {
    new FeatureModelAnalysisCLITool(args);
  }

  public FeatureModelAnalysisCLITool(String[] args) {
    try {
      CommandLineParser parser = new BasicParser();
      cmd = parser.parse(getOptions(), args);
      processArguments();
      tool.performAnalyses();
      printResults();
    }
    catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }

  public static Options getOptions() {
    Options options = new Options();
    options.addOption(ARG_FD, true, "set feature diagram");
    options.addOption(ARG_FC, true, "set feature configuration");
    options.addOption(ARG_IS_VALID, false, "check if <fc> is a valid configuration in <fd>");
    options.addOption("h", ARG_HELP, false, "print help");
    return options;
  }

  protected void processArguments() {
    if (null == cmd || cmd.hasOption(ARG_HELP)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("FeatureModelAnalysisTool", getOptions(), true);
      return;
    }
    ASTFeatureDiagram fd = processFDArgument();
    Optional<ASTFeatureConfiguration> fc = processFCArgument(fd.getName());

    if (cmd.hasOption(ARG_IS_VALID) && hasFCArgument("IsValid")) {
      isValid = Optional.of(new IsValid(fc.get()));
      tool.addAnalysis(isValid.get());
    }
    //TODO add other analyses here
  }

  protected void printResults() {
    if(isValid.isPresent()){
      Optional<Boolean> result = isValid.get().getResult();
      if(!result.isPresent() || null == result.get()){
        Log.error("0x919 There was an error conducting the 'isValid' analysis!");
      }
      System.out.println("Result isValid: "+result.get());
    }
  }

  protected ASTFeatureDiagram processFDArgument() {
    if (cmd.hasOption(ARG_FD)) {
      ASTFeatureDiagram ast = FeatureDiagramTool.run(cmd.getOptionValue(ARG_FD));
      tool = new FeatureModelAnalysisTool(ast.getSymbol());
      return ast;
    }
    else {
      Log.error("0xFC900 Performing an analysis requires to add a feature diagram as argument!");
      return null;
    }
  }

  protected boolean hasFCArgument(String analysisName) {
    if (!cmd.hasOption(ARG_FC)) {
      Log.error("0xFC903 Performing an '" + analysisName
          + "' analysis requires to add a feature configuration as argument!");
      return false;
    }
    return true;
  }

  protected Optional<ASTFeatureConfiguration> processFCArgument(String fdName) {
    if (cmd.hasOption(ARG_FC)) {
      ASTFeatureConfiguration ast = FeatureConfigurationTool.run(cmd.getOptionValue(ARG_FD));
      if (!ast.getFdNameSymbol().getFullName().equals(fdName)) {
        Log.error(
            "0xFC902 The feature configuration '" + ast.getName() + "' is for the feature diagram '"
                + ast.getFdNameSymbol().getFullName()
                + "' that does not match the passed feature diagram `" + fdName + "`!");
      }
      return Optional.of(ast);
    }
    return Optional.empty();
  }

}
