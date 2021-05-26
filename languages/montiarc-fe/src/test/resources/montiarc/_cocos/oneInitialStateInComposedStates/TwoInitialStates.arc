/* (c) https://github.com/MontiCore/monticore */
package oneInitialStateInComposedStates;

component TwoInitialStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;


  // invalid model, because there is no unique initial state
  automaton {
    initial state A{
      // this is fine
      initial state AA;
      state AB;
      AA -> AB;
    };

    state B;

    state C {
      // this is not fine, because there are two initials
      initial state CA;
      initial state CB;
      state CC;
    };

    state D;
  }
}