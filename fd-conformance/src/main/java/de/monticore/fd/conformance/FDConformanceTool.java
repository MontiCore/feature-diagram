package de.monticore.fd.conformance;

import de.monticore.fd.conformance.fdmapping._ast.ASTFDMapping;
import de.monticore.fd.conformance.loader.FDLoader;
import de.monticore.featurediagram.FeatureDiagramToolTOP;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

public class FDConformanceTool extends FeatureDiagramToolTOP {

  public static void main(String[] args) {
    FDConformanceTool tool = new FDConformanceTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();
    options = addAdditionalOptions(options);
    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || !cmd.getArgList().isEmpty() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar fdConformance.jar", options, true);
        return;
      }

      // Set input file and parse it
      if (!cmd.hasOption("c")) {
        Log.error("0xFD102 conformance checking require a  concrete model");
      }
      String concrete = cmd.getOptionValue("c");

      // Set input file and parse it
      if (!cmd.hasOption("r")) {
        Log.error("0xFD102 conformance checking require a  concrete model");
      }
      String reference = cmd.getOptionValue("r");

      // Set input file and parse it
      if (!cmd.hasOption("m")) {
        Log.error("0xFD102conformance checking require a  mapping ");
      }
      String mapping = cmd.getOptionValue("m");

      // check conformance
      checkConformance(reference, concrete, mapping);

    } catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar MCFeatureDiagram.jar", options, true);
      Log.error("0xFD114 An exception occured while processing the CLI input!", e);
    }
  }

  @Override
  public Options addAdditionalOptions(Options options) {
    // help
    options.addOption(
        Option.builder("c")
            .longOpt("concrete")
            .desc("Introduce the concrete model file")
            .numberOfArgs(1)
            .build());

    options.addOption(
        Option.builder("r")
            .longOpt("reference")
            .desc("Introduce reference model file ")
            .numberOfArgs(1)
            .build());

    options.addOption(
        Option.builder("m")
            .longOpt("map")
            .desc("Introduce mapping file ")
            .numberOfArgs(1)
            .build());

    return options;
  }

  public void checkConformance(String refPath, String conPath, String mappingPath) {
    String logName = this.getClass().getSimpleName();
    Log.info("Loading and checking reference model.....", logName);
    ASTFDCompilationUnit ref = FDLoader.loadAndCheckFD(refPath);

    Log.info("Loading and checking concrete model.....", logName);
    ASTFDCompilationUnit con = FDLoader.loadAndCheckFD(conPath);

    Log.info("Loading and checking  mapping.....", logName);
    ASTFDMapping mapping = FDLoader.loadAndCheckMapping(refPath, conPath, mappingPath);

    Log.println("===== Check if "+ con.getFeatureDiagram().getName() + " conforms to " + ref.getFeatureDiagram().getName() + " with respect to " + mapping.getName() + " =====");
    FDConformanceChecker.checkConformance(ref, con, mapping);
  }
}
