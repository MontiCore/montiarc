/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

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
  
  public String top() { return TOP; }
  public String component() { return COMPONENT; }
  public String builder() { return BUILDER; }
  public String input() { return INPUT; }
  public String output() { return OUTPUT; }
  public String context() { return CONTEXT; }
  public String automaton() { return AUTOMATON; }
  public String states() { return STATES; }
}