package automaton.invalid;

component LowerCaseAutomaton {
  port
    in Integer a;
    
  var int c;
  
  automaton lowercaseName {
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
