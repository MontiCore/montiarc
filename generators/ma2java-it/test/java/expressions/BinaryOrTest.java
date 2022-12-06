/* (c) https://github.com/MontiCore/monticore */
package expressions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BinaryOrTest {

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
    BinaryOr or = new BinaryOr();
    or.setUp();
    or.init();
    or.getA().update(a);
    or.getB().update(b);

    // When
    or.compute();

    // Then
    assertThat(or.getR().getValue()).isEqualTo(a | b);
  }
}
