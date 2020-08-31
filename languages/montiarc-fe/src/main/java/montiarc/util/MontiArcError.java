/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all MontiArc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum MontiArcError implements montiarc.util.Error {
  COMPONENT_AND_FILE_NAME_DIFFER("0xMA1000", "The name of the component \" %s \" is not identical "
    + "to the name of the file \" %s \" (without its file-extension)."),
  COMPONENT_AND_FILE_PACKAGE_DIFFER("0xMA1001", "The package declaration \" %s \" of component "
    + " \" %s \" is different from the package \" %s \" of the file.");

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