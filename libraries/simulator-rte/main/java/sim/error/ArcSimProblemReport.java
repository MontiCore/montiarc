/* (c) https://github.com/MontiCore/monticore */
package sim.error;

/**
 * ProblemReport used in the ArcSim runtime.
 */
public class ArcSimProblemReport {

  /**
   * Possible problem report types.
   */
  public enum Type {
    ERROR,
    FATAL_ERROR,
    INFO,
    WARNING
  }

  /**
   * Where does the report occure.
   */
  private final String component;

  /**
   * Message to show.
   */
  private final String message;

  /**
   * When has the report been produced.
   */
  private final int timeUnit;

  /**
   * Type of the report.
   */
  private final Type type;

  /**
   * Creates a new ProblemReport with the given parameters.
   *
   * @param type    type from the report
   * @param message message from the report
   */
  public ArcSimProblemReport(final Type type, final String message) {
    this(type, message, -1, null);
  }

  /**
   * Creates a new ProblemReport with the given parameters.
   *
   * @param type      type from the report
   * @param message   message from the report
   * @param component from which component the report has been reported
   */
  public ArcSimProblemReport(final Type type, final String message, final String component) {
    this(type, message, -1, component);
  }

  /**
   * Creates a new ProblemReport with the given parameters.
   *
   * @param type      type from the report
   * @param message   message from the report
   * @param timeUnit  when does the report has been reported
   * @param component from which component the report has been reported
   */
  public ArcSimProblemReport(final Type type, final String message, final int timeUnit, final String component) {
    this.type = type;
    this.message = message;
    this.timeUnit = timeUnit;
    this.component = component;
  }

  /**
   * @return the component
   */
  public String getComponent() {
    return component;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @return the timeUnit
   */
  public int getTimeUnit() {
    return timeUnit;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append(type.toString());
    sb.append("]");
    if (getTimeUnit() != -1) {
      sb.append(" timeunit: '");
      sb.append(getTimeUnit());
      sb.append("'");
    }
    if (getComponent() != null) {
      sb.append(" in ");
      sb.append(getComponent());
    }
    sb.append(": ");
    sb.append(getMessage());
    return sb.toString();
  }
}
