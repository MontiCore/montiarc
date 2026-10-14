/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.google.common.base.Preconditions;
import montiarc.util.Error;

/**
 * The enum of all arc2fd errors, which extends the mixin
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xA2F00 - 0xA2FFF
 * </p>
 */
public enum Arc2FDError implements Error {
  IO_EXCEPTION("0xA2F00", "Cannot write feature diagram '%s': '%s'"),
  OUTPUT_DIRECTORY("0xA2F01", "Cannot create output directory '%s': '%s'"),
  RECURSIVE_COMPONENT("0xA2F02", "Component '%s' contains its own name as feature"),
  SMT_CONVERSION_ERROR("0xA2F03", "Cannot convert the constraints of component '%s' to SMT formulae"),
  SMT_UNSAT("0xA2F06", "The constraints of component '%s' are unsatisfiable"),
  SMT_UNKNOWN("0xA2F07", "The constraints of component '%s' cannot be determined"),
  SMT_STRICTNESS("0xA2F08", "The constraints of component '%s' may be stricter than the feature diagram"),
  DEAD_FEATURE("0xA2F09", "Feature '%s' of component '%s' can never be selected"),
  EMPTY_COMPONENT("0xA2F0A", "Component '%s' has no features"),
  INVALID_NAME("0xA2F0B", "The name '%s' in component '%s' must not contain '%s', which is reserved for flattening subcomponent features");

  private final String errorCode;
  private final String errorMsgFormat;

  Arc2FDError(String errorCode, String errorMsgFormat) {
    Preconditions.checkNotNull(errorCode);
    Preconditions.checkNotNull(errorMsgFormat);
    Preconditions.checkArgument(ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMsgFormat = errorMsgFormat;
  }

  /**
   * @return The unique error code of this error.
   */
  public String getErrorCode() { return this.errorCode; }

  /**
   * @return The error message of this error.
   */
  public String getErrorMsgFormat() { return this.errorMsgFormat; }

  public String toString() { return this.getErrorCode() + ": " + this.getErrorMsgFormat(); }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format call. The number of arguments has to
   *             match the string defined in {@link #getErrorMsgFormat()}
   * @return properly formatted error message
   */
  public String format(Object... args) { return String.format(toString(), args); }
}
