/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all MontiArc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum MontiArcError implements montiarc.util.Error {
  COMPONENT_AND_FILE_NAME_DIFFER("0xMA1000", "The name of the component \" %s \" is not identical "
    + "to the name of the file \" %s \" (without its file-extension)."),
  COMPONENT_AND_FILE_PACKAGE_DIFFER("0xMA1001", "The package declaration \" %s \" of component "
    + " \" %s \" is different from the package \" %s \" of the file."),
  TOOL_PARSE_IOEXCEPTION("0xMA1002", "Could not parse the file \" %s \"."),
  TOOL_FILE_WALK_IOEXCEPTION("0xMA1003", "Could not access the directory \" %s \" or one of its subdirectories."),
  CLI_INPUT_OPTION_MISSING("0xMA1004",  "Option '%s' is missing, but an input is required"),
  CLI_INPUT_FILE_NOT_EXIST("0xMA1005", "Input file '%s' does not exist\n"),
  CLI_OPTION_AMBIGUOUS("0xMA1006", "Option '%s' does not match any valid option"),
  CLI_OPTION_UNRECOGNIZED("0xMA1007", "Unrecognized option '%s'"),
  CLI_OPTION_MISSING("0xMA1008", "Options [%s] are missing, but are required"),
  CLI_ARGUMENT_MISSING("0xMA1009", "Option '%s' is missing an argument");

  private final String errorCode;
  private final String errorMessage;

  MontiArcError(String errorCode, String errorMessage) {
    assert (errorCode != null);
    assert (errorMessage != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
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
  public String printErrorMessage() {
    return this.errorMessage;
  }

  @Override
  public String toString() {
    return this.errorCode + ": " + this.printErrorMessage();
  }
}