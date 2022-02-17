/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasHierarchicalState {
  automaton {
    state Foo {
      state Bar;
    };

    initial Foo / { /* empty initial output declaration */ };
  }
}