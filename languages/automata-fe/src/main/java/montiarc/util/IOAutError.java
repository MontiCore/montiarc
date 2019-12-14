/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all IOAutomaton errors. Extends the mixing interface {@Link montiarc.util.Error}
 */
public enum IOAutError implements montiarc.util.Error {
  ;

  private final String errorCode;

  IOAutError(String errorCode) {
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