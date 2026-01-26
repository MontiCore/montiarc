/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.logging;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests for {@link DataFormatter}
 */
public class DataFormatterTest {

  enum AnEnum {
    A;
  }

  static class AClass {

  }

  @ParameterizedTest
  @MethodSource("testFormatArguments")
  void testFormat(Object data, String expected) {
    // Given
    // When
    String actual = DataFormatter.format(data);

    // Then
    Assertions.assertEquals(expected, actual);
  }

  public static Stream<Arguments> testFormatArguments() {
    return Stream.of(
      Arguments.of(null, "null"),
      Arguments.of(true, "true"),
      Arguments.of(5, "5"),
      Arguments.of(5L, "5"),
      Arguments.of(5F, "5.0"),
      Arguments.of(5D, "5.0"),
      Arguments.of('a', "'a'"),
      Arguments.of("abc", "\"abc\""),
      Arguments.of(Tick.get(), "Tick"),
      Arguments.of(AnEnum.A, "A"),
      Arguments.of(new AClass(), "Object of montiarc.rte.logging.DataFormatterTest.AClass"),
      Arguments.of(Message.of(null), "null"),
      Arguments.of(Message.of(true), "true"),
      Arguments.of(Message.of(5), "5"),
      Arguments.of(Message.of(5L), "5"),
      Arguments.of(Message.of(5F), "5.0"),
      Arguments.of(Message.of(5D), "5.0"),
      Arguments.of(Message.of('a'), "'a'"),
      Arguments.of(Message.of("abc"), "\"abc\""),
      Arguments.of(Message.of(Tick.get()), "Tick"),
      Arguments.of(Message.of(AnEnum.A), "A"),
      Arguments.of(Message.of(new AClass()), "Object of montiarc.rte.logging.DataFormatterTest.AClass")
    );
  }
}
