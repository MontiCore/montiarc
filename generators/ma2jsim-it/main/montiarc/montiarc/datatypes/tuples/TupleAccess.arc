/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.tuples;

component TupleAccess {
  port sync in ((int, boolean), double) input;
  port sync out int first;
  port sync out boolean flag;
  port sync out double value;
  port sync out (boolean, int) swapped;

  automaton {
    initial state S;

    S -> S / {
      (int, boolean) inner = input[0];
      first = inner[0];
      flag = input[0][1];
      value = input[1];
      swapped = (inner[1], inner[0] + 1);
    }
  }
}
