/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParamsBool2Test {

  /**
   * @param p1 the argument for parameter p1
   * @param p2 the argument for parameter p2
   */
  @ParameterizedTest
  @ValueSource(booleans = {
    true, true,
    true, false,
    false, true,
    false, false
  })
  @Disabled
  void testCtorSetsField(boolean p1, boolean p2) {
    // When
    ParamsBool2Comp sut = new ParamsBool2CompBuilder()
      .set_param_p1(p1)
      .set_param_p2(p2)
      .setName("sut").build();

    // Then
    //Assertions.assertThat(sut.field_v1).isEqualTo(p1);
    //Assertions.assertThat(sut.field_v2).isEqualTo(p2);
  }
}
