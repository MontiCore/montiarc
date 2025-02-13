/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.inheritance;

import montiarc.types.Person;

component WithPersonIn {
  port sync in Person person;

  automaton {
    initial state S;
    S -> S / { };
  }
}
