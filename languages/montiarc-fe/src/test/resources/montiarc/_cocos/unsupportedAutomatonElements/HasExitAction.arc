/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasExitAction {
  automaton {
    state Foo {
      exit / { }
    };

    initial Foo / { /* empty initial output declaration */ };
  }
}