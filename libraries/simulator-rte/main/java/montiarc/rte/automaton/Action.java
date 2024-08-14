/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents an action in a state machine.
 * @param <T> the type of the message that triggers the action. Use {@link NoInput} for actions without argument.
 */
public interface Action<T> {

  /**
   * Executes the action.
   */
  void execute(T msg);

}
