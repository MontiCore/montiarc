/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all IOAutomaton errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum IOAutError implements montiarc.util.Error {
  ;

  private final String errorCode;
  private final String errorMessage;

  IOAutError(String errorCode, String errorMessage) {
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
    return this.getErrorCode() + ": " + this.printErrorMessage();
  }
}