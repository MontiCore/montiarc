/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.generic.Message;

/**
 * 
 * 
 * 
 */
public interface ComponentDummyPortInterface {

  public void p1InMessageReceived(Message<String> message);

  public void p2InMessageReceived(Message<String> message);

}
