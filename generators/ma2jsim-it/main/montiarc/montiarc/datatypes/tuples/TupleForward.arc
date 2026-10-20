/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.tuples;

component TupleForward {
  port sync in (int, boolean) input;
  port sync out ((int, boolean), double) output;

  (int, boolean) fallback = (0, false);

  automaton {
    initial state S;

    S -> S / {
      (int, boolean) forwarded = input;
      output = (forwarded, 1.0);
      fallback = (1, true);
    }
  }
}
