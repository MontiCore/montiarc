/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component Hierarchy {

  port in int i;
  port out String o;

  <<sync>> automaton {
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

    A1 -> C11 [i==1] / { o = "first";};
    A1 -> A  [i == 2] / {o = "second";};
    A -> C11 [i == 3] / {o = "third";};
    A -> C [i == 4] / {o = "fourth";};
    A -> B2 [i == 5] / {o = "fifth";};
    C11 -> B [i == 6] / {o = "sixth";};
  }
}
