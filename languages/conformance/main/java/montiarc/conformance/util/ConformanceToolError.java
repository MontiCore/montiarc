/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import montiarc.util.Error;

public enum ConformanceToolError implements Error {

  MISSING_CONCRETE_MODEL("0xFD102", 
    "conformance checking require a concrete model. "
    + "A statechart as embedded in a montiArc component (*.arc) and "
    + "A Class diagram containing the datatypes (.cd)"),
  MISSING_REFERENCE_MODEL("0xFD103",
    "conformance checking require a reference model. "
    + "A statechart as embedded in a montiArc component (*.arc) and "
    + "A Class diagram containing the datatypes (.cd)"),
  MISSING_MAPPING("0xFD104", "conformance checking require a mapping between concrete and reference model.");

  private final String errorCode;
  private final String errorMsgFormat;

  ConformanceToolError(String errorCode, String errorMsgFormat) {
    this.errorCode = errorCode;
    this.errorMsgFormat = errorMsgFormat;
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getErrorMsgFormat() {
    return errorMsgFormat;
  }

  public String format(Object... args) {
    if (args.length == 0) {
      return getErrorCode() + " " + getErrorMsgFormat();
    }
    return getErrorCode() + " " + String.format(getErrorMsgFormat(), args);
  }
}
