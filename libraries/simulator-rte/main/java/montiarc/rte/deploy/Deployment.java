/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.Simulation;
import montiarc.rte.component.Component;
import montiarc.rte.component.SimComponent;
import montiarc.rte.deploy.util.DeSerializer;
import montiarc.rte.oracle.OracleFactory;
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
      if (cmd.hasOption("debug")) {
        Log.initDEBUG();
      }
      if (cmd.hasOption("tickCount")) {
        tickCount = Long.parseLong(cmd.getOptionValue("tickCount"));
      }
      if (cmd.hasOption("simulationTickLength")) {
        simulationTickLength = Long.parseLong(cmd.getOptionValue("simulationTickLength")) * 1000000;
      }
      if (cmd.hasOption("simulatedTickLength")) {
        simulatedTickLength = Long.parseLong(cmd.getOptionValue("simulatedTickLength")) * 1000000;
      }

      for (Option opt : cmd.getOptions()) {
        parameters.put(opt.getLongOpt(), cmd.getOptionValue(opt.getLongOpt()));
      }
    } catch (ParseException | NumberFormatException e) {
      Log.error(e.getMessage());
      printHelp();
      return;
    }

    deploy(
      tickCount,
      simulationTickLength,
      simulatedTickLength,
      parameters
    );
  }

  public void deploy(
    Long tickCount,
    Long simulationTickLength,
    Long simulatedTickLength,
    Map<String, String> parameters
  ) {

    // Setup
    Simulation.nanosecondsPerTick = simulatedTickLength <= 0 ? simulationTickLength : simulatedTickLength;
    CoordinatingScheduler scheduler = buildCoordinatingScheduler();
    T component = Preconditions.checkNotNull(buildComponent(scheduler, parameters));
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
    return new CoordinatingScheduler(OracleFactory.withDefaultStrategy(OracleFactory.preferFirst()));
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
    options.addOption(Option.builder().longOpt("debug")
      .required(false)
      .desc("Output debug level log messages")
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
      HelpFormatter.builder().setShowSince(false).get().printHelp("Deployment", "", buildOptions(), "", true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
