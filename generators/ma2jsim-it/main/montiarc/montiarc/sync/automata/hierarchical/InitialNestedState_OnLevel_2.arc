/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

component InitialNestedState_OnLevel_2 {

  port out String o;

  <<sync>> automaton {

    initial state A {
      initial state AA {
        initial state AAA;  // <- We want to check that this state is entered
        state AAB;
      };
      state AB {
        initial state ABA;
        state ABB;
      };
    };

    state B {
      initial state BA {
        initial state BAA;
        state BAB;
      };
      state BB {
        initial state BBA;
        state BBB;
      };
    };

    AAA -> AAA / o = "AAA";;
    AAB -> AAB / o = "AAB";;
    ABA -> ABA / o = "ABA";;
    ABB -> ABB / o = "ABB";;

    BAA -> BAA / o = "BAA";;
    BAB -> BAB / o = "BAB";;
    BBA -> BBA / o = "BBA";;
    BBB -> BBB / o = "BBB";;
  }
}
