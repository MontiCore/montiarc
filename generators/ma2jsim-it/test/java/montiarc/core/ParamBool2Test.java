/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
  @Disabled
  void testCtorSetsField(boolean p) {
    // When
    ParamBool2Comp sut = new ParamBool2CompBuilder()
      .set_param_p(p)
      .setName("sut").build();

    // Then
    //Assertions.assertThat(sut.field_v).isEqualTo(p);
  }
}
