/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduler;

import montiarc.rte.msg.Message;
import montiarc.rte.port.TimeAwarePortForComposition;
import montiarc.rte.scheduling.FifoSchedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeAwarePortForCompositionTest {
  @Test
  void testBuffer(){
    // Given
    SchedulerComp comp = new SchedulerCompBuilder().setScheduler(new FifoSchedule()).setName("test").build();
    TimeAwarePortForComposition<Integer> buffer = new TimeAwarePortForComposition<>("test", comp);
    // When
    for(int i = 0; i < 10; i ++){
      buffer.receive(new Message<>(i));
    }

    // Then
    Assertions.assertThat(buffer.pollBuffer().getData()).isEqualTo(0);
    Assertions.assertThat(buffer.peekBuffer().getData()).isEqualTo(1);
    Assertions.assertThat(buffer.pollLastBuffer().getData()).isEqualTo(9);
    Assertions.assertThat(buffer.peekLastBuffer().getData()).isEqualTo(8);

  }
}
