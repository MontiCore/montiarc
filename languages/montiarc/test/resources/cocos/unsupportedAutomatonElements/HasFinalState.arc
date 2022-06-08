/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasFinalState {
  automaton {
    initial state Foo;

    final state Bar;
  }
}