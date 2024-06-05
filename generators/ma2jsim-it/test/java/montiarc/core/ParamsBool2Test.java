/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.rte.msg.Message;
import montiarc.rte.port.ITimeAwareInPort;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParamsBool2Test {

  /**
   * capture of the actual output stream on port o1
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual_o1;

  /**
   * the target port of output port o1
   */
  @Mock
  ITimeAwareInPort<Boolean> port_o1;

  /**
   * capture of the actual output stream on port o2
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual_o2;

  /**
   * the target port of output port o2
   */
  @Mock
  ITimeAwareInPort<Boolean> port_o2;

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
