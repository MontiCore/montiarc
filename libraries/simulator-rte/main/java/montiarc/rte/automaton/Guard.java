/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a guard (or precondition) in a state machine.
 */
public interface Guard {

  /**
   * Checks the condition.
   *
   * @return whether the condition holds
   */
  boolean check();

}
