/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

/**
 * Port that plays a role both as the target, as well as the source of a connector.
 * E.g., a subcomponent instance receives messages through its incoming ports, but
 * if it is decomposed, then it forwards these messages to its own subcomponents.
 */
public interface InOutPort<I, O> extends InPort<I>, OutPort<O> {

  void forwardWithoutRemoval();
}
