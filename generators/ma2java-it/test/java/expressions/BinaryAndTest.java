/* (c) https://github.com/MontiCore/monticore */
package expressions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BinaryAndTest {

  @ParameterizedTest
  @CsvSource(value = {
    "0, 0",
    "0, 1",
    "0, -1",
    "0, 2147483647",
    "0, -2147483648",
    "1, 0",
    "1, 1",
    "1, -1",
    "1, 2147483647",
    "1, -2147483648",
    "-1, 0",
    "-1, 1",
    "-1, -1",
    "-1, 2147483647",
    "-1, -2147483648",
    "2147483647, 0",
    "2147483647, 1",
    "2147483647, -1",
    "2147483647, 2147483647",
    "2147483647, -2147483648",
    "-2147483648, 0",
    "-2147483648, 1",
    "-2147483648, -1",
    "-2147483648, 2147483647",
    "-2147483648, -2147483648",
  })
  public void shouldProduceExpectedOutput(int a, int b) {
    // Given
    BinaryAnd and = new BinaryAnd();
    and.setUp();
    and.init();
    and.getA().update(a);
    and.getB().update(b);

    // When
    and.compute();

    // Then
    assertThat(and.getR().getValue()).isEqualTo(a & b);
  }
}
