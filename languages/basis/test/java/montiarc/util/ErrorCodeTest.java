/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests that Error code pattern {@link Error} works as intended.
 */
public class ErrorCodeTest extends AbstractTest {

  @ParameterizedTest
  @ValueSource(strings = { "0xC1000", "0xC1001", "0xC0100"})
  public void shouldMatchErrorCodePattern(String errorCode) {
    Assertions.assertTrue(Error.ERROR_CODE_PATTERN.matcher(errorCode).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = { "", "1000", "0xC", "0xC0", "0xC1", "0xC100", "0xC00001"})
  public void shouldMismatchErrorCodePattern(String errorCode) {
    Assertions.assertFalse(Error.ERROR_CODE_PATTERN.matcher(errorCode).matches());
  }
}