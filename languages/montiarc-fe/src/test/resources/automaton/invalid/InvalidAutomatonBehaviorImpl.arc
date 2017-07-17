package automaton.invalid;

component InvalidAutomatonBehaviorImpl {
  port
    in Integer a;

  automaton InvalidAutomatonBehaviorImpl {
  	variable int c;
  
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
  
  automaton DuplicatedAutomatonImpl {  
  	state Start;
    initial Start;

    Start;
  }
  
  automaton lowercaseName {
  	variable int c;
  
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
