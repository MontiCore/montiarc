/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.logging;

/** Has constants with different logging aspects that can be added to log messages */
public class Aspects {
  private Aspects() {}

  public static final String CREATE_OBJECT = "create_object";

  public static final String CREATE_COMP = "create_comp";

  public static final String REMOVE_COMP = "remove_comp";

  public static final String CREATE_CONNECTOR = "create_connector";

  public static final String REMOVE_CONNECTOR = "remove_connector";

  public static final String SEND_MSG = "send";

  public static final String RECEIVE_MSG = "receive";

  public static final String ENTER_STATE = "enter_state";

  public static final String MODE_CHANGE = "mode_change";

  public static final String RECEIVE_EVENT = "event";
}
