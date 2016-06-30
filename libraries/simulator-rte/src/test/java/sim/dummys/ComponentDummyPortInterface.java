package sim.dummys;

import sim.generic.Message;

/**
 * 
 * @author Arne Haber
 * @version 27.11.2008
 * 
 * 
 */
public interface ComponentDummyPortInterface {

  public void p1InMessageReceived(Message<String> message);

  public void p2InMessageReceived(Message<String> message);

}
