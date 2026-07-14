/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PortObserverTest {

  <T> void reveiveAll(PortObserver<T> port, T[] messages){
    for (T msg: messages){
      if (msg == Tick.get() ){
        port.receive(Tick.get());
      }else{
        port.receive(Message.of(msg));
      }
    }
  }

  @Test
  void test_ObservedValues_removesTicks(){
    var port = new PortObserver<>();
    reveiveAll(port, new Object[]{Tick.get(),1,2, Tick.get(),3,4,5,Tick.get()});
    Assertions.assertEquals(List.of(1,2,3,4,5), port.getObservedValues());
  }

  @Test
  void test_ObservedValues_empty(){
    var port = new PortObserver<>();
    Assertions.assertTrue(port.isBufferEmpty());
    Assertions.assertEquals(List.of(), port.getObservedValues());
  }

  @Test
  void test_DropMessagesIgnoredBySync(){
    var port = new PortObserver<>();
    reveiveAll(port, new Object[]{Tick.get(),1,2, Tick.get(),3,4,5,Tick.get()});
    port.dropMessagesIgnoredBySync();
    Assertions.assertEquals(List.of(2,5), port.getObservedValues());
  }

  @Test
  void test_DropMessagesIgnoredBySync_empty(){
    var port = new PortObserver<>();
    Assertions.assertTrue(port.isBufferEmpty());
    port.dropMessagesIgnoredBySync();
    Assertions.assertEquals(List.of(), port.getObservedValues());
  }

  @Test
  void test_DropMessagesIgnoredBySync_lastElement(){
    var port = new PortObserver<>();
    reveiveAll(port, new Object[]{Tick.get(),1,2, Tick.get(),3,4,5});
    port.dropMessagesIgnoredBySync();
    Assertions.assertEquals(List.of(2,5), port.getObservedValues());
  }
}
