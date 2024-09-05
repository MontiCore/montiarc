/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import montiarc.rte.logging.Aspects;

/**
 * Facade for logging aspects of {@link Aspects} into that the generator can access
 */
@SuppressWarnings("unused")
public class LogAspects {

  protected LogAspects() { }

  protected static LogAspects instance = new LogAspects();

  public static LogAspects getInstance() {
    return instance;
  }

  public String createComponent() {
    return Aspects.CREATE_COMP;
  }

  public String createObject() {
    return Aspects.CREATE_OBJECT;
  }

  public String sendMSG() {
    return Aspects.SEND_MSG;
  }

  public String receiveMSG() {
    return Aspects.RECEIVE_MSG;
  }

  public String changeState() {
    return Aspects.ENTER_STATE;
  }

  public String receiveEvent() {
    return Aspects.RECEIVE_EVENT;
  }
}
