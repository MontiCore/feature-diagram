/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial.prettyprint.FeatureConfigurationPartialPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IGlobalScope;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;
import tool.analyses.*;
import tool.solver.ChocoSolver;
import tool.solver.ISolver;
import tool.transform.FZNModelBuilder;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.trafos.BasicTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;
import tool.transform.trafos.RootFeatureSelected;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * FACT is the main class of the Feature Diagram Language
 *
 * It provides a CLI processing inputs and commands to
 * perform analyses on FDs, provide symtabs and pretty printing
 *
 */
public class FACT {
  
  /**
   * Main function: Delegates to the FACT instance it creates
   * @param args command line arguments (e.g. --help)
   */
  public static void main(String[] args) {
    Log.initWARN();
    new FACT(args);
  }
  
  /**
   * Uses Apache CLI Parser to access args
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
      ASTFDCompilationUnit fcu = parseModel(readModelName(cmd));
      ASTFeatureDiagram fd = fcu.getFeatureDiagram();

      xxxtool = new FeatureModelAnalysisTool(fd);   // XXX ??? toDel
      
      // handle --findValid
      if (cmd.hasOption("findValid")) {
        execFindValid(fd);
      }
      // ... place for further analyses

      // TODO BR2AB: ich habe findValid mal rausoperiert und dargestellt, wie ich mir das
      //  vorstellen würde. Denn das furchtbar komplexe Meta-geschraube das hier eingebaut ist
      // ist zwar sehr systematisch, aber leider auch Geschwür-artig komplex zu durchschauen.
      // Deshalb die Vereinfachungen, die sie bitte für die anderen Optionen nachziehen
      // BITTE in Zukunft wieder FUNKTIONAL denken und keine Meta-Generischen Überstrukturen
      // aufbauen
      
      if (processArguments(cmd,fd)) {
        xxxtool.performAnalyses();   // does not handle findValid anymore
        printResults();
      }
    } catch (ParseException e) {
      Log.error("0xFC901 Error while parsing the command line options!", e);
    }
  }
  
  /**
   * handle --findValid
   */
  public void execFindValid(ASTFeatureDiagram fd) {
    // instantiate the analysis
    FindValidConfig analysis = new FindValidConfig();

    // choose the solver
    ISolver solver = new ChocoSolver();
    solver.setFeatureDiagrammName(fd.getName());
  
    // TODO: das wirkt immer noch alles etwas metagenerisch
    FZNModelBuilder modelPrinter = new FZNModelBuilder(false);
    List<FeatureModel2FlatZincModelTrafo> trafos = new ArrayList<>();
    trafos.add(new BasicTrafo());
    trafos.add(new RootFeatureSelected());
    trafos.add(new ComplexConstraint2FZN());
    modelPrinter.addAllFeatureModelFZNTrafos(trafos);
    modelPrinter.buildFlatZincModel(fd);
    analysis.setFeatureModel(fd);
    String s = modelPrinter.getFlatZincModel().print();
    
    // Hier wird die eigentliche Analyse dann nun wirklich gemacht ...
    // TODO: Ernsthaft? This should be really optimized to not produce all and then select only one
    
    Collection<ASTFeatureConfiguration> configurations =
                solver.solve(s, fd.getAllFeatures(), modelPrinter.isAllSolutions());

    if (configurations.isEmpty()) {
      Log.error("0xFC774 findValid was not successful");
    }
    else {
      ASTFeatureConfiguration res = configurations.iterator().next();
      System.out.println("Result of findValid: " + FeatureConfigurationPartialPrettyPrinter.print(res));
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Reading the FD name from commandline as first and only(!) argument
   * @param cmd
   * @return FD name
   */
  protected String readModelName(CommandLine cmd) {
    if (1 != cmd.getArgList().size()) {
      Log.error("0xFD231 Argument number must be 1, but is " + cmd.getArgList().size());
      return null; // normally not reached
    } else {
      return cmd.getArgList().get(0).toString();
    }
  }
  
  // For Serialization and De-Serialization
  protected static final FeatureDiagramScopeDeSer deser = new FeatureDiagramScopeDeSer();
  
  // Where to find and store symbols:
  // TODO: das müssen wir noch flexibilisieren, denn
  // 1) input symbols: die sind mit dem Modelpath verheiratet und müsen in vielen Pfaden gefunden werden können
  // 2) output symbols: die sind evtl. extra anzugeben als konkrete Stelle - ggf. mit Dateinamen (wobei hier ein Default existiert)
  public static final Path SYMBOL_LOCATION = Paths.get("target/symbols");
  
  /**
   * Parsing the FD in a wellformed form:
   * i.e. CoCos are checked, SymbolTable created
   * @param pathname
   * @return the FD AST
   */
  protected ASTFDCompilationUnit parseModel(String pathname) {
  
    // Phase 1: -----------------------------------------------
    // parse the model and create the AST representation
    Optional<ASTFDCompilationUnit> optFD = null;
    try {
      // Use the standard Parser: (we do not use a milöl here)
      FeatureDiagramParser parser = new FeatureDiagramParser();
      optFD = parser.parse(pathname);

      // handle errors
      if (!parser.hasErrors() && optFD.isPresent()) {
        Log.info(pathname + " parsed successfully!", "FeatureDiagramTool");
      } else {
        Log.error("0xFD200 Model could not be parsed.");
        return null; // normally not reached
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFD201 Failed to parse." + pathname, e);
    }
    ASTFDCompilationUnit ast1 = optFD.get();
    
    // reconstruct modelpath from input file
    // TODO: erklären wozu? Kann der nicht auch aus pathname hier rekonstruiert werden
    Path path = Paths.get(pathname).toAbsolutePath().getParent();
    if (ast1.isPresentPackage()) {
      for (int i = 0; i < ast1.getPackage().sizeParts(); i++) {
        path = path.getParent();
      }
    }
  
    // Phase 2: -----------------------------------------------
    // setup the symbol table
    // 2.1: global scope
    IGlobalScope gs = FeatureDiagramMill
            .featureDiagramGlobalScopeBuilder()
            .setModelPath(new ModelPath(path, SYMBOL_LOCATION))
            .setModelFileExtension("fd")
            .build();

    // 2.2: symbol table
    FeatureDiagramArtifactScope modelTopScope = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope((IFeatureDiagramGlobalScope) gs)  // TODO: cast rausnehmen, sobald signatur angepasst
        .build()
        .createFromAST(ast1);
  
    // 2.3: execute default context conditions
    FeatureDiagramCoCos.checkAll(ast1);
  
    // Phase 3: -----------------------------------------------
    // by default:
    // store artifact scope after context conditions have been checked
    // TODO: hier rausnehmen (weil als Default ersetzbar; CLI sollte das auf Kommando machen und die Stelle definierbar angeben)
    // Beispielsweise gehen diese Argumente:
    // -symtab <stname-und-zwar-der-ganze-path....>
    // -s = use default symtabname
    deser.store(modelTopScope, SYMBOL_LOCATION);
  
    return ast1;
  }
  
  /***********************************************************************
   * **********************************************************************
   * **********************************************************************
   * nachfolgendes muss verschlankt und kommentiert werden
   */

  @Deprecated
  public static final String ARG_IS_VALID = "isValid";
  public static final String ARG_ALL_PRODUCTS = "allProducts";
  public static final String ARG_DEAD_FEATURES = "dead";
  public static final String ARG_FALSE_OPTIONAL_FEATURES = "falseOpt";
  // public static final String ARG_FIND_VALID_PRODUCT = "findValid";
  public static final String ARG_FILTER = "filter";
  public static final String ARG_IS_VOID = "isVoid";
  public static final String ARG_NUMBER_OF_PRODUCTS = "numProducts";
  
  @Deprecated
  public static final String XXXARG_HELP = "help";
  
  @Deprecated
  protected FeatureModelAnalysisTool xxxtool;
  
  @Deprecated
  protected Optional<IsValid> isValid = Optional.empty();
  protected Optional<AllProducts> allProducts = Optional.empty();
  protected Optional<DeadFeature> dead = Optional.empty();
  protected Optional<FalseOptional> falseOpt = Optional.empty();
  // protected Optional<FindValidConfig> xxxfindValid = Optional.empty();
  protected Optional<Filter> filter = Optional.empty();
  protected Optional<IsVoidFeatureModel> isVoid = Optional.empty();
  protected Optional<NumberOfProducts> numProducts = Optional.empty();
  
  
  
  
  @Deprecated
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
    createAnalysisOption(options, "findValid", null, "find a valid" +
            " configuration for the passed feature model");
    createAnalysisOption(options, ARG_IS_VOID, null, "check if the passed " +
            "feature model has any valid configuration");
    createAnalysisOption(options, ARG_NUMBER_OF_PRODUCTS, null, "calculate " +
            "the number of valid configurations for the passed feature model");
    // ... place for further analyses

    options.addOption("h", XXXARG_HELP, false, "show this explanation");
    return options;
  }
  
  @Deprecated
  protected boolean processArguments(CommandLine cmd, ASTFeatureDiagram fd) {
    /*
    if (null == cmd || cmd.hasOption(XXXARG_HELP)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar FACT.jar <test.fd> [analysis options] ", getOptions());
      return false;
    }
    ASTFeatureDiagram fd = processFDArgument(cmd);
   */
  
    if (cmd.hasOption(ARG_IS_VALID)) {
      ASTFeatureConfiguration fc = processFCArgument("IsValid", ARG_IS_VALID, fd.getName(), cmd);
      isValid = Optional.of(new IsValid(fc));
      xxxtool.addAnalysis(isValid.get());
    }
    if (cmd.hasOption(ARG_ALL_PRODUCTS)) {
      allProducts = Optional.of(new AllProducts());
      xxxtool.addAnalysis(allProducts.get());
    }
    if (cmd.hasOption(ARG_DEAD_FEATURES)) {
      dead = Optional.of(new DeadFeature());
      xxxtool.addAnalysis(dead.get());
    }
    if (cmd.hasOption(ARG_FALSE_OPTIONAL_FEATURES)) {
      falseOpt = Optional.of(new FalseOptional());
      xxxtool.addAnalysis(falseOpt.get());
    }
    if (cmd.hasOption(ARG_FILTER)) {
      ASTFeatureConfiguration fc = processFCArgument("Filter", ARG_FILTER,
              fd.getName(), cmd);
      filter = Optional.of(new Filter(fc));
      xxxtool.addAnalysis(filter.get());
    }
/*    if (cmd.hasOption("findValid")) {
      xxxfindValid = Optional.of(new FindValidConfig());
      xxxtool.addAnalysis(xxxfindValid.get());
    }
*/
    if (cmd.hasOption(ARG_IS_VOID)) {
      isVoid = Optional.of(new IsVoidFeatureModel());
      xxxtool.addAnalysis(isVoid.get());
    }
    if (cmd.hasOption(ARG_NUMBER_OF_PRODUCTS)) {
      numProducts = Optional.of(new NumberOfProducts());
      xxxtool.addAnalysis(numProducts.get());
    }
    // ... place for further analyses
    return true;
  }

  protected static void createAnalysisOption(Options options, String key, String argName,
                                             String description) {
    Option opt = new Option(key, argName != null, description);
    if (argName != null){opt.setArgName(argName);}
    options.addOption(opt);
  }
  
  @Deprecated
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
        }
      );
    }
/*
    if (xxxfindValid.isPresent()) {
      Optional<ASTFeatureConfiguration> result = xxxfindValid.get().getResult();
      if (!result.isPresent()) {
        Log.error("0xFC924 There was an error conducting the 'findValid' " +
          "analysis!");
      } else {
        result.get();
      }
      System.out.println("Result of findValid: " + FeatureConfigurationPartialPrettyPrinter.print(result.get()));
    }
*/
    if (isVoid.isPresent()) {
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
  
  @Deprecated
  protected ASTFeatureDiagram processFDArgument(CommandLine cmd) {
    if (0 == cmd.getArgList().size()) {
      Log.error("0xFC900 No feature diagram given as first argument!");
      return null;
    } else if (1 == cmd.getArgList().size()) {
      ASTFeatureDiagram ast = FeatureDiagramTool.run(cmd.getArgList().get(0).toString());
      xxxtool = new FeatureModelAnalysisTool(ast);   // XXX ???
      return ast;
    } else {
      for (int i = 1; i < cmd.getArgList().size(); i++) {
        Log.error("0xFC999 Unknown arguments '" + cmd.getArgList().get(i) + "'");
      }
      return null;
    }
  }

  protected ASTFeatureConfiguration processFCArgument(String analysisName,
                                                      String analysisArg, String fdName, CommandLine xxxcmd) {
    if (xxxcmd.hasOption(analysisArg)) {
      ASTFeatureConfiguration ast = FeatureConfigurationTool.run(xxxcmd.getOptionValue(analysisArg));
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
