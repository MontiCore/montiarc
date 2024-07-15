/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import montiarc.conformance.util.AutomataLoader;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.Pair;
import scmapping.mapping2smt.AutomataMapping;
import scmapping.mapping2smt.MCMapping;

public class AutConformanceTool extends MontiArcTool {

  public static void main(String[] args) {
    AutConformanceTool tool = new AutConformanceTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args) {
    Log.init();
    AutomataLoader.initMills();
    Options options = initOptions();
    options = addAdditionalOptions(options);
    try {

      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || !cmd.getArgList().isEmpty() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar SCConformance.jar", options, true);
        return;
      }

      // Set input file and parse it
      if (!cmd.hasOption("c")) {
        Log.error(
            "0xFD102 conformance checking require a  concrete model. "
                + "A statechart as embedded in a montiArc component (*.arc) and "
                + "A Class diagram containing the datatypes (.cd)");
      }
      String[] concrete = cmd.getOptionValues("c");

      // Set input file and parse it
      if (!cmd.hasOption("r")) {
        Log.error(
            "0xFD102 conformance checking require a  concrete model."
                + " A statechart as embedded in a montiArc component (*.arc) and"
                + " A Class diagram containing the datatypes (.cd)");
      }
      String[] reference = cmd.getOptionValues("r");

      // Set input file and parse it
      if (!cmd.hasOption("m")) {
        Log.error(
            "0xFD102conformance checking require a  mapping  between concrete and reference model.");
      }
      String mapping = cmd.getOptionValue("m");

      // check conformance
      boolean isConform = checkConformance(reference, concrete, mapping);
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
            .desc("Introduce the concrete statechart and class diagram")
            .numberOfArgs(2)
            .build());

    options.addOption(
        Option.builder("r")
            .longOpt("reference")
            .desc("Introduce the reference statechart and class diagram")
            .numberOfArgs(2)
            .build());

    options.addOption(
        Option.builder("m")
            .longOpt("mapping")
            .desc("Introduce the file mapping concrete and reference statechart")
            .numberOfArgs(1)
            .build());

    return options;
  }

  public boolean checkConformance(String[] reference, String[] concrete, String mappingPath) {
    String logName = this.getClass().getSimpleName();
    Log.info("Loading and checking reference Model.....", logName);
    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> ref = loadModels(reference);

    Log.info("Loading and checking concrete Model.....", logName);
    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> con = loadModels(concrete);

    Log.info("Loading and checking mapping.....", logName);
    AutomataMapping mapping =
        new MCMapping(
            AutomataLoader.loadMapping(
                mappingPath,
                ref.getValue().getComponentType(),
                ref.getKey(),
                con.getValue().getComponentType(),
                con.getKey()));

    Log.info("Checking Conformance..... ", logName);
    boolean isConform =
        new AutomataConfChecker()
            .isConform(
                ref.getValue().getComponentType(),
                con.getValue().getComponentType(),
                ref.getKey(),
                con.getKey(),
                mapping);
    if (isConform) {
      Log.info("Models are conform.", logName);
    } else {
      Log.warn("Models are NOT conform.");
    }
    return isConform;
  }

  private Pair<ASTCDCompilationUnit, ASTMACompilationUnit> loadModels(String[] files) {
    Optional<String> cd = Arrays.stream(files).filter(f -> f.endsWith(".cd")).findFirst();
    Optional<String> aut = Arrays.stream(files).filter(f -> f.endsWith(".arc")).findFirst();

    if (cd.isPresent() && aut.isPresent()) {
      return AutomataLoader.loadModels(new File(aut.get()), new File(cd.get()));
    } else {
      Log.error("Either the statechart of the class diagram is failing");
      assert false;
      return null;
    }
  }
}
