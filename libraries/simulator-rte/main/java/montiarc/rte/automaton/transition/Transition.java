/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.transition;

import montiarc.rte.automaton.State;

/**
 * Represents a transition in a  MontiArc automaton.
 * <br>
 * Aside from a source and target state, a transition should also
 * have a method representing the execution of the transition.
 * This method should take an instance of the respective owning component as a context.
 */
public interface Transition {
  State getSourceState();

  State getTargetState();

}
