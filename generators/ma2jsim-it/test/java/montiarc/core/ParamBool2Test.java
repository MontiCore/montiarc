/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JSimTest
class ParamBool2Test {

  /**
   * @param p the argument for parameter p
   */
  @ParameterizedTest
  @ValueSource(booleans = {
    true,
    false
  })
  void testSetsField(boolean p) {
    // When
    ParamBool2CompImpl sut = (ParamBool2CompImpl) new ParamBool2CompBuilder()
      .set_param_p(p)
      .setName("sut").build();

    // Then
    assertThat(sut.field_v).isEqualTo(p);
  }
}
