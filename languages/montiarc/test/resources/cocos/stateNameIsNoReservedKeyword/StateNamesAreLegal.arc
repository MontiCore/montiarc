/* (c) https://github.com/MontiCore/monticore */
package stateNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component StateNamesAreLegal {
  automaton {
    initial state foo;
    state bar;
  }
}
