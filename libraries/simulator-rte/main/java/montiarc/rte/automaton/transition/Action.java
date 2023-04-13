/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.transition;

/**
 * Represents a transition action in a MontiArc model.
 * <p>
 * This is a functional interface.
 */
public interface Action {

  /**
   * Perform hte transition action. This method is expected to operate on the transition/automaton's owner.
   */
  void perform();

}
