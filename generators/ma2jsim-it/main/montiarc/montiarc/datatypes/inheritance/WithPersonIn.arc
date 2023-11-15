/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.inheritance;

import montiarc.types.Person;

component WithPersonIn {
  port in Person person;

  <<sync>> automaton {
    initial state S;
    S -> S / { };
  }
}
