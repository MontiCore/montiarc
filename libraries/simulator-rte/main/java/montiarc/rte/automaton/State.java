/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a state in a MontiArc automaton.
 */
public class State {

  public State(String name) {
    this.name = name;
  }

  String name;

  public String name() {
    return this.name;
  }

  public void enter() { }

  public void exit() { }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }
}
