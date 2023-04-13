/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a state in a MontiArc automaton.
 * <p>
 * A state should define an entry and exit action which operates on a given context.
 * Said methods cannot be defined in this interface because the context would need to be
 * of a concrete type (because the component's interface has to be accessible in the actions).
 */
public interface State {

}
