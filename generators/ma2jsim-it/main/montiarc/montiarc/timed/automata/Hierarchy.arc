/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Hierarchy {

  port in int i;
  port out String o;

  <<timed>> automaton {
    initial state A {
      initial state A1;
      state A2 {
        state A21 {
          initial state A211;
        };
      };
      state A3 {
        initial state A31;
      };
    };
    state B {
      initial state B1;
      state B2;
      state B3;
    };
    state C {
      initial state C1 {
        initial state C11;
      };
    };

    A1 -> C11 [i==1] i / { o = "first";};
    A1 -> A  [i == 2] i / {o = "second";};
    A -> C11 [i == 3] i / {o = "third";};
    A -> C [i == 4] i / {o = "fourth";};
    A -> B2 [i == 5] i / {o = "fifth";};
    C11 -> B [i == 6] i / {o = "sixth";};
  }
}
