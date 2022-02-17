/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasFinalState {
  automaton {
    state Foo;
    initial Foo / { /* empty initial output declaration */ };

    final state Bar;
  }
}