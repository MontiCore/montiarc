/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component SwitchString {

  port sync in String i;
  port sync out int o;

  automaton {
    initial state S;

    S -> S / {
      switch (i) {
        case "hello": { o = 1; }
        case "world": { o = 2; }
        case "test":  { o = 3; }
        default:      { o = 0; }
      }
    }
  }
}
