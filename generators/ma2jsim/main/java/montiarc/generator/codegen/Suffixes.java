/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

@SuppressWarnings("unused")
public class Suffixes {
  
  protected Suffixes() {}
  
  protected final static Suffixes instance = new Suffixes();
  
  public static Suffixes getInstance() {
    return instance;
  }
  
  public final static String TOP = "TOP";
  public final static String COMPONENT = "Comp";
  public final static String BUILDER = "Builder";
  public final static String INPUT = "Input";
  public final static String OUTPUT = "Output";
  public final static String CONTEXT = "Context";
  public final static String AUTOMATON = "Automaton";
  public final static String STATES = "States";
  public final static String MODE_AUTOMATON = "ModeAutomaton";
  public final static String MODES = "Modes";
  public final static String CONTEXT_FOR_MODES = "ContextForModes";
  public final static String COMPUTE = "Compute";
  public final static String FIELDS = "Fields";
  public final static String PARAMETERS = "Parameters";
  public final static String FEATURES = "Features";
  public final static String EVENTS = "Events";
  public final static String SYNC_MSG = "SyncMsg";
  public final static String MSG_GUARD = "MsgGuard";
  public final static String MSG_Action = "MsgAction";

  public String top() { return TOP; }
  public String component() { return COMPONENT; }
  public String builder() { return BUILDER; }
  public String input() { return INPUT; }
  public String output() { return OUTPUT; }
  public String context() { return CONTEXT; }
  public String automaton() { return AUTOMATON; }
  public String states() { return STATES; }
  public String modeAutomaton() { return MODE_AUTOMATON; }
  public String modes() { return MODES; }
  public String contextForModes() { return CONTEXT_FOR_MODES; }
  public String compute() { return COMPUTE; }
  public String fields() { return FIELDS; }
  public String parameters() { return PARAMETERS; }
  public String features() { return FEATURES; }
  public String events() { return EVENTS; }
  public String syncMsg() { return SYNC_MSG; }
  public String msgGuard() { return MSG_GUARD; }
  public String msgAction() { return MSG_Action; }
}
