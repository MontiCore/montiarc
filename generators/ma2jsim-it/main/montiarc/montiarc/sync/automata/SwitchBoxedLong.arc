/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component SwitchBoxedLong {

  port sync in Long i;
  port sync out int o;

  automaton {
    initial state S;

    S -> S / {
      switch (i) {
        case 100L: o = 30;
        case 200L: o = 40;
        default:   o = -1;
      }
    }
  }
}
