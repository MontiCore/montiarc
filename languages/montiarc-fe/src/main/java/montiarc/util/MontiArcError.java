/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all MontiArc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum MontiArcError implements montiarc.util.Error {
  ;

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