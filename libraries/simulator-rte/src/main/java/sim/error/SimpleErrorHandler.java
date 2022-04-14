/* (c) https://github.com/MontiCore/monticore */
/*
 * 31.07.2009
 */
package sim.error;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of an error handler that may be used in the simulation environment.
 */
public class SimpleErrorHandler implements ISimulationErrorHandler {

  private final Logger logger;

  /**
   * List of occurred errors.
   */
  protected List<ArcSimProblemReport> errors;

  /**
   *
   */
  public SimpleErrorHandler() {
    errors = new LinkedList<ArcSimProblemReport>();
    this.logger = LoggerFactory.getLogger(getClass());
  }

  /**
   * @see sim.error.ISimulationErrorHandler#init()
   */
  @Override
  public void init() {

  }

  /**
   * @see sim.error.ISimulationErrorHandler#addReport(ArcSimProblemReport)
   */
  @Override
  public void addReport(ArcSimProblemReport report) {
    this.errors.add(report);
    switch (report.getType()) {
      case ERROR:
        getLogger().error(report.toString());
        break;
      case FATAL_ERROR:
        getLogger().error(report.toString());
        break;
      case INFO:
        getLogger().info(report.toString());
        break;
      case WARNING:
        getLogger().warn(report.toString());
        break;
      default:
        getLogger().debug(report.toString());
        break;
    }
  }

  /**
   * @see sim.error.ISimulationErrorHandler#getReports()
   */
  @Override
  public List<ArcSimProblemReport> getReports() {
    return this.errors;
  }

  /**
   * @see sim.error.ISimulationErrorHandler#getLogger()
   */
  @Override
  public Logger getLogger() {
    return this.logger;
  }
}
