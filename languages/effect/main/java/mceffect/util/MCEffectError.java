/* (c) https://github.com/MontiCore/monticore */
package mceffect.util;

import montiarc.util.Error;

/**
 * The enum of all MCEffect errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xEFF00 - 0xEFF99
 */
public enum MCEffectError implements Error {
  PACKAGE_MISMATCH("0xEFF00", "The package declaration %s of the diagram (%s) must not differ from the package of the diagram file"),
  MODEL_PATH_MISSING("0xEFF01", "A path to the models must be given. Consider using the option -mp to introduce the model path"),
  MAIN_COMPONENT_MISSING("0xEFF02", "A main component must be specified. Consider using the option -mc to introduce the name of main component"),
  EFFECT_SPECIFICATIONS_MISSING("0xEFF03", "Effects specifications must be given. Consider using the option -e to introduce the effect file"),
  COMPONENT_TYPE_NOT_SPECIFIED("0xEFF04", "The type of component must be specified. Consider using options --ma for MontiArc component and --sml for sysML components"),
  INVALID_PORT_REFERENCE("0xEFF10", "Not a valid port: %s"),
  INVALID_TAG("0xEFF11", "Not a valid tag: %s"),
  INVALID_TAG_NAME("0xEFF12", "Invalid tag-name for effects: %s");

  private final String errorCode;
  private final String errorMsgFormat;

  MCEffectError(String errorCode, String errorMsgFormat) {
    assert (errorCode != null);
    assert (errorMsgFormat != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMsgFormat = errorMsgFormat;
  }

  /**
   * @return The unique error code of this error.
   */
  @Override
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String getErrorMsgFormat() {
    return this.errorMsgFormat;
  }

  @Override
  public String toString() {
    return this.errorCode + ": " + this.getErrorMsgFormat();
  }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #getErrorMsgFormat()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}
