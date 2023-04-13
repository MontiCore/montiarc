/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.transition;

/**
 * Represents a guard statement in a MontiArc model.
 * <p>
 * This is a functional interface.
 */
public interface Guard {

  /**
   * Check the guard condition. This method is expected to operate on the transition/automaton's owner.
   *
   * @return true if the guard condition is fulfilled
   */
  boolean check();

}
