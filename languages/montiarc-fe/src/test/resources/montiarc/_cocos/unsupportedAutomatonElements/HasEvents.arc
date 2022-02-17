/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasEvents {
  port in int a;

  automaton {
    state Foo;
    initial Foo / { /* empty initial output declaration */ };

    Foo -> Foo <a>;
  }
}