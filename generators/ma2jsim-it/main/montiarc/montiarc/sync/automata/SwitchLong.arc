/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component SwitchLong {

  port sync in long i;
  port sync out int o;

  automaton {
    initial state S;

    S -> S / {
      switch (i) {
        case 0L: o = 10; break;
        case 1L: o = 20; break;
        default: o = -1; break;
      }
    }
  }
}
