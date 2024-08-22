/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.rte.msg.Message;
import montiarc.rte.port.InPort;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParamBool2Test {

  /**
   * capture of the actual output stream on port o
   */
  @Captor
  ArgumentCaptor<Message<Boolean>> actual;

  /**
   * the target port of output port o
   */
  @Mock
  InPort<Boolean> port_o;

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
