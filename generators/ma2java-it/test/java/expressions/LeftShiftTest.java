/* (c) https://github.com/MontiCore/monticore */
package expressions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class LeftShiftTest {

  @ParameterizedTest
  @CsvSource(value = {
    "0, 0",
    "0, 1",
    "0, -1",
    "0, 31",
    "0, -31",
    "0, 32",
    "0, -32",
    "1, 0",
    "1, 1",
    "1, -1",
    "1, 31",
    "1, -31",
    "1, 32",
    "1, -32",
    "-1, 0",
    "-1, 1",
    "-1, -1",
    "-1, 31",
    "-1, -31",
    "-1, 32",
    "-1, -32",
    "2147483647, 0",
    "2147483647, 1",
    "2147483647, -1",
    "2147483647, 31",
    "2147483647, -31",
    "2147483647, 32",
    "2147483647, -32",
    "-2147483648, 0",
    "-2147483648, 1",
    "-2147483648, -1",
    "-2147483648, 31",
    "-2147483648, -31",
    "-2147483648, 32",
    "-2147483648, -32",
  })
  public void shouldProduceExpectedOutput(int data, int bits) {
    // Given
    LeftShift shift = new LeftShift();
    shift.setUp();
    shift.init();
    shift.getD().update(data);
    shift.getB().update(bits);

    // When
    shift.compute();

    // Then
    assertThat(shift.getR().getValue()).isEqualTo(data << bits);
  }
}
