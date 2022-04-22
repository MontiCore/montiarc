/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.message.Message;

public interface ComponentDummyPortInterface {

  void p1InMessageReceived(Message<String> message);

  void p2InMessageReceived(Message<String> message);

}
