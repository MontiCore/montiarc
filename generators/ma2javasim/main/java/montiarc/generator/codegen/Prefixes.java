/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

public class Prefixes {
  
  protected Prefixes() {}
  
  protected final static Prefixes instance = new Prefixes();
  
  public static Prefixes getInstance() {
    return instance;
  }
  
  public final static String DEPLOY = "Deploy";
  public final static String PORT = "port_";
  public final static String PARAMETER = "param_";
  public final static String FIELD = "field_";
  public final static String FEATURE = "feature_";
  public final static String SUBCOMP = "subcomp_";
  public final static String SETTER_METHOD = "set_";
  public final static String GETTER_METHOD = "get_";
  public final static String STATE = "state_";
  public final static String TRANSITION = "transition_";
  public final static String MESSAGE = "msg_";
  public final String TICK = "tick_";
  public final static String NO_STIMULUS = "noStimulus_";
  
  public String deploy() { return DEPLOY; }
  public String port() { return PORT; }
  public String parameter() { return PARAMETER; }
  public String field() { return FIELD; }
  public String feature() { return FEATURE; }
  public String subcomp() { return SUBCOMP; }
  public String setterMethod() { return SETTER_METHOD; }
  public String getterMethod() { return GETTER_METHOD; }
  public String state() { return STATE; }
  public String transition() { return TRANSITION; }
  public String message() { return MESSAGE; }
  public String tick() { return TICK; }
  public String noStimulus() { return NO_STIMULUS; }
}
