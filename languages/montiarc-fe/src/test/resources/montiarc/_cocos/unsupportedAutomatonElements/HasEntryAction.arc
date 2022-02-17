/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasEntryAction {
  automaton {
    state Foo {
      entry / { }
    };

    initial Foo / { /* empty initial output declaration */ };
  }
}