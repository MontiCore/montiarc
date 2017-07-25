package automaton.invalid;

component MutipleBehaviors {
  port
    in Integer a;

  var int c;

  automaton InvalidAutomatonBehaviorImpl {
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
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
