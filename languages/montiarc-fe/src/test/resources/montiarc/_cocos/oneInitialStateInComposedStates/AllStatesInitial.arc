/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInComposedStates;

component AllStatesInitial {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid model, because all substates are initial
  automaton {
    state A;
    state B {
      initial state BA;
      initial state BB;
    };
    state C;
    initial state D {
      initial state DA;
      initial state DB;
      initial state DC;
      initial state DD;
    };
  }
}