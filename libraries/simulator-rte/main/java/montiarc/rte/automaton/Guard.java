/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a guard (or precondition) in a state machine.
 * @param <T> the type of the message that the guard can consult. Use {@link NoInput} for guards without argument.
 */
public interface Guard<T> {

  /**
   * Checks the condition.
   *
   * @return whether the condition holds
   */
  boolean check(T msg);

}
