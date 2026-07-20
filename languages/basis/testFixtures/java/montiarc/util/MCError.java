/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import java.util.regex.Pattern;

/**
 * Wraps monticore error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum MCError implements Error {
  MISSING_COMPONENT("0xD0104"),
  AMBIGUOUS_COMPONENT_REFERENCE("0xD0105");

  public static final Pattern ERROR_CODE_PATTERN = Pattern.compile("0xA\\d{4}");

  private final String errorCode;

  MCError(String errorCode) {
    assert (errorCode != null);
    this.errorCode = errorCode;
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getErrorMsgFormat() {
    throw new UnsupportedOperationException();
  }
}
