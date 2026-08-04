/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import de.monticore.cd4code.CD4CodeMill;
import de.se_rwth.commons.logging.Log;
import mceffect.util.MCEffectError;
import montiarc.MontiArcMill;
import montiarc.util.MCError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Set;

public class MCEffectTool extends MCEffectToolTOP {

  public static void main(String[] args) {
    MCEffectTool tool = new MCEffectTool();
    tool.run(args);
  }

  public static void initMills() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();

    MontiArcMill.reset();
    MontiArcMill.init();
    MontiArcMill.globalScope().clear();
  }

  @Override
  public void run(String[] args) {
    Log.init();
    initMills();
    Options options = initOptions();
    options = addAdditionalOptions(options);
    try {

      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      if (null == cmd || !cmd.getArgList().isEmpty() || cmd.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar MCEffect.jar", options, true);
        return;
      }

      // Set input file and parse it
      if (!cmd.hasOption("mp")) {
        Log.error(
            MCEffectError.MODEL_PATH_MISSING.format());
      }
      String modelPath = cmd.getOptionValue("mp");

      // Set input file and parse it
      if (!cmd.hasOption("mc")) {
        Log.error(
            MCEffectError.MAIN_COMPONENT_MISSING.format());
      }
      String mainComponent = cmd.getOptionValue("mc");

      // Set input file and parse it
      if (!cmd.hasOption("e")) {
        Log.error(
            MCEffectError.EFFECT_SPECIFICATIONS_MISSING.format());
      }
      String[] effects = cmd.getOptionValues("e");
      boolean showGraph = cmd.hasOption('g');

        // check run tool
      if (cmd.hasOption("sml")) {
        MCFullEffectChecker.checkSysMLComponent(
            modelPath, mainComponent, Set.of(effects), showGraph);
      } else if (!cmd.hasOption("ma")) {
        MCFullEffectChecker.checkMontiArcComponent(
            modelPath, mainComponent, Set.of(effects), showGraph);
      } else {
        Log.error(
            MCEffectError.COMPONENT_TYPE_NOT_SPECIFIED.format());
      }

    } catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar MCFeatureDiagram.jar", options, true);
      Log.error(MCError.CLI_EXCEPTION.format(), e);
    }
  }

  @Override
  public Options addAdditionalOptions(Options options) {
    // help
    options.addOption(
        Option.builder("mp")
            .longOpt("modelpath")
            .desc(
                "Introduce the path to the models: montiArc components(*.arc),"
                    + " effect specifications(*.eff), and class diagrams(*.cd)")
            .numberOfArgs(1)
            .get());

    options.addOption(
        Option.builder("mc")
            .longOpt("maincomponent")
            .desc("Introduce the Qualified name of the component form the model directory\n")
            .numberOfArgs(1)
            .get());

    options.addOption(
        Option.builder("e")
            .longOpt("effect")
            .desc("Introduce the file containing the different effects(*.eff)")
            .numberOfArgs(1)
            .get());

    options.addOption(
        Option.builder("ma")
            .longOpt("montiarc")
            .desc("indicate that the tool must run on montiarc components")
            .numberOfArgs(0)
            .get());
    options.addOption(
        Option.builder("sml")
            .longOpt("sysml")
            .desc("indicate that the tool must run on sysML components")
            .numberOfArgs(0)
            .get());
    options.addOption(
        Option.builder("g")
            .longOpt("graph")
            .desc("show a graph with ports and their connections")
            .numberOfArgs(0)
            .get());
    return options;
  }
}
