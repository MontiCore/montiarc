/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component FieldReferencing {
  int x = 1;
  int z = x + y;
  int y = x + 1 + w;
  int w = 3 + x;

  port sync out int o;

  automaton {
    initial state S;

    S -> S / {
      o = z;
    };
  }
}
