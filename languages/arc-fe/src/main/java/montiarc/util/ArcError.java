/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all Arc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum ArcError implements montiarc.util.Error {
  ;

  private final String errorCode;

  ArcError(String errorCode) {
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