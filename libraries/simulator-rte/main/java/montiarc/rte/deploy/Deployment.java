/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.component.SimComponent;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.scheduling.CoordinatingScheduler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Deployment<T extends Component> {

  protected DeSerializer deSerializer;
  protected DeploymentStrategy<T> strategy = null;

  public Deployment() {
    deSerializer = new DeSerializer();
  }

  public Deployment(DeploymentStrategy<T> strategy) {
    this();
    this.strategy = strategy;
    strategy.setDeSerializer(deSerializer);
  }

  public void deploy(String[] args) {
    Log.initWARN();
    Log.enableFailQuick(false);

    DefaultParser parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get();
    long tickCount = Long.MIN_VALUE;
    long simulationTickLength = 1000000;
    long simulatedTickLength = 0;
    Map<String, String> parameters = new HashMap<>();

    try {
      CommandLine cmd = parser.parse(buildOptions(), args);

      if (cmd.hasOption("help")) {
        printHelp();
        return;
      }
      if (cmd.hasOption("tickCount")) {
        tickCount = Long.parseLong(cmd.getOptionValue("tickCount"));
      }
      if (cmd.hasOption("simulationTickLength")) {
        simulationTickLength = Long.parseLong(cmd.getOptionValue("simulationTickLength"));
      }
      if (cmd.hasOption("simulatedTickLength")) {
        simulationTickLength = Long.parseLong(cmd.getOptionValue("simulatedTickLength"));
      }

      for (Option opt : cmd.getOptions()) {
        parameters.put(opt.getLongOpt(), cmd.getOptionValue(opt.getLongOpt()));
      }
    } catch (ParseException | NumberFormatException e) {
      Log.error(e.getMessage());
      printHelp();
      return;
    }

    // Setup
    CoordinatingScheduler scheduler = buildCoordinatingScheduler();
    T component = Objects.requireNonNull(buildComponent(scheduler, parameters));
    if (strategy != null) {
      strategy.connect(component, parameters);
    }

    // Execution
    scheduler.run((SimComponent) component, false, tickCount, simulationTickLength, simulatedTickLength);

    // Cleanup
    if (strategy != null) {
      strategy.disconnect();
    }
  }

  protected CoordinatingScheduler buildCoordinatingScheduler() {
    return new CoordinatingScheduler();
  }

  protected abstract T buildComponent(CoordinatingScheduler scheduler, Map<String, String> parameters);

  protected Options buildOptions() {
    // Default Options
    Options options = new Options();
    options.addOption(Option.builder()
      .required(false)
      .longOpt("tickCount")
      .desc("Sets the length of the simulation, i.e. how many ticks are executed (by default runs indefinitely)")
      .hasArg().argName("count")
      .get());
    options.addOption(Option.builder().longOpt("simulationTickLength")
      .required(false)
      .desc("Sets the real-world tick interval in milliseconds (by default: 1 ms). Use a value of 0 to run the simulation as fast as possible")
      .hasArg().argName("length")
      .get());
    options.addOption(Option.builder().longOpt("simulatedTickLength")
      .required(false)
      .desc("Sets the simulation-world tick interval in milliseconds (by default: same as the simulationTickLength)")
      .hasArg().argName("length")
      .get());
    options.addOption(Option.builder().longOpt("help")
      .required(false)
      .desc("Displays this help message")
      .get());

    // Parameter Options
    addOptionsForParameters(options);

    if (strategy != null) {
      strategy.addCLIOptions(options);
    }

    return options;
  }

  protected void addOptionsForParameters(Options options) { }

  protected void printHelp() {
    try {
      HelpFormatter.builder().get().printHelp("java " + this.getClass().getSimpleName(), "", buildOptions(), "", true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
