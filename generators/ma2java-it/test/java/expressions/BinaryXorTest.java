/* (c) https://github.com/MontiCore/monticore */
package expressions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BinaryXorTest {

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
    BinaryXor xor = new BinaryXor();
    xor.setUp();
    xor.init();
    xor.getA().update(a);
    xor.getB().update(b);

    // When
    xor.compute();

    // Then
    assertThat(xor.getR().getValue()).isEqualTo(a ^ b);
  }
}
