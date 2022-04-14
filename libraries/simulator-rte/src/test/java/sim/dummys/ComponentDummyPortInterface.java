/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.generic.Message;

public interface ComponentDummyPortInterface {

  void p1InMessageReceived(Message<String> message);

  void p2InMessageReceived(Message<String> message);

}
