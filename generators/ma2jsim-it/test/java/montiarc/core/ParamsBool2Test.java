/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class ParamsBool2Test {

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   */
  @ParameterizedTest
  @CsvSource({
    "true, true",
    "true, false",
    "false, true",
    "false, false"
  })
  void testSetsField(boolean p1, boolean p2) {
    // When
    ParamsBool2CompImpl sut = (ParamsBool2CompImpl) new ParamsBool2CompBuilder()
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();

    // Then
    assertThat(sut.field_v1).isEqualTo(p1);
    assertThat(sut.field_v2).isEqualTo(p2);
  }
}
