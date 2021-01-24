/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

/**
 * Tests that Error code pattern {@see Error} works as intended.
 */
public class ErrorCodeTest extends AbstractTest {

  @Override protected Pattern supplyErrorCodePattern() {
    return Error.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @ValueSource(strings = { "0xMA1000", "0xMA1001", "0xMA10000000", "0xMA100000000000" })
  public void shouldMatchErrorCodePattern(String errorCode) {
    Assertions.assertTrue(Error.ERROR_CODE_PATTERN.matcher(errorCode).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = { "", "1000", "0xMA", "0xMA0", "0xMA1", "0xMA100", "0xMA0100",
      "0xMA10001", "0xMA01000000" })
  public void shouldMismatchErrorCodePattern(String errorCode) {
    Assertions.assertFalse(Error.ERROR_CODE_PATTERN.matcher(errorCode).matches());
  }
}