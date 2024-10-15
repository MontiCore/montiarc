/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

component InitialNestedState_OnLevel_1 {

  port out String o;

  <<sync>> automaton {

    initial state A {
      initial state AA;  // <- We want to check that this state is entered
      state AB;
    };

    state B {
      initial state BA;
      state BB;
    };

    AA -> AA / o = "AA";;
    AB -> AB / o = "AB";;
    BA -> BA / o = "BA";;
    BB -> BB / o = "BB";;

  }
}
