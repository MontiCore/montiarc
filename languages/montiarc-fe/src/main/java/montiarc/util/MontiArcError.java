/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all MontiArc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum MontiArcError implements montiarc.util.Error {
  COMPONENT_AND_FILE_NAME_DIFFER("0xMA1000"),
  COMPONENT_AND_FILE_PACKAGE_DIFFER("0xMA1001");

  private final String errorCode;

  MontiArcError(String errorCode) {
    assert (errorCode != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String toString() {
    return errorCode;
  }
}