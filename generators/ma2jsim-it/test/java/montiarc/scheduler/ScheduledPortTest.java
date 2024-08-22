/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduler;

import montiarc.rte.msg.Message;
import montiarc.rte.port.ScheduledPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ScheduledPortTest {
  @Test
  void testBuffer(){
    // Given
    SchedulerComp comp = new SchedulerCompBuilder().setName("test").build();
    ScheduledPort<Integer> buffer = new ScheduledPort<>("test", comp, comp.getScheduler());
    // When
    for(int i = 0; i < 10; i ++){
      buffer.receive(new Message<>(i));
    }

    comp.run();

    // Then
    Assertions.assertThat(buffer.pollBuffer().getData()).isEqualTo(0);
    Assertions.assertThat(buffer.peekBuffer().getData()).isEqualTo(1);
    Assertions.assertThat(buffer.pollLastBuffer().getData()).isEqualTo(9);
    Assertions.assertThat(buffer.peekLastBuffer().getData()).isEqualTo(8);

  }
}
