/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import de.monticore.cd4code.CD4CodeMill;
import de.se_rwth.commons.logging.Log;
import java.util.Set;
import montiarc.MontiArcMill;
import org.apache.commons.cli.*;

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
            "0xEFF001 A path to the models must be give. Consider using the option"
                + " -mp to introduce the model path.");
      }
      String modelPath = cmd.getOptionValue("mp");

      // Set input file and parse it
      if (!cmd.hasOption("mc")) {
        Log.error(
            "0xEFF001 A main component must be specified. Consider using the option"
                + " -mc to introduce the name of main component.");
      }
      String mainComponent = cmd.getOptionValue("mc");

      // Set input file and parse it
      if (!cmd.hasOption("e")) {
        Log.error(
            "0xEFF001 Effects specifications must be given. Consider Using the option -e to introduce the effect file");
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
            "0xEFF001 the type of component mus tbe specified.\n"
                + "consider using options --ma for MontiArc component and --sml for sysML Components.");
      }

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
        Option.builder("mp")
            .longOpt("modelpath")
            .desc(
                "Introduce the path to the models: montiArc components(*.arc),"
                    + " effect specifications(*.eff), and class diagrams(*.cd)")
            .numberOfArgs(1)
            .build());

    options.addOption(
        Option.builder("mc")
            .longOpt("maincomponent")
            .desc("Introduce the Qualified name of the component form the model directory\n")
            .numberOfArgs(1)
            .build());

    options.addOption(
        Option.builder("e")
            .longOpt("effect")
            .desc("Introduce the file containing the different effects(*.eff)")
            .numberOfArgs(1)
            .build());

    options.addOption(
        Option.builder("ma")
            .longOpt("montiarc")
            .desc("indicate that the tool must run on montiarc components")
            .numberOfArgs(0)
            .build());
    options.addOption(
        Option.builder("sml")
            .longOpt("sysml")
            .desc("indicate that the tool must run on sysML components")
            .numberOfArgs(0)
            .build());
    options.addOption(
        Option.builder("g")
            .longOpt("graph")
            .desc("show a graph with ports and their connections")
            .numberOfArgs(0)
            .build());
    return options;
  }
}
