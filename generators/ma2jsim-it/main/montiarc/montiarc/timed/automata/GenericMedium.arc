/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component GenericMedium<T> {

  port in T i;
  port out T o;

  <<timed>> automaton {
    initial state S;

    S -> S i / {
      o = i;
    };
  }
}
