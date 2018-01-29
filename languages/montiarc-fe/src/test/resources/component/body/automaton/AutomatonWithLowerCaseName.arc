package component.body.automaton;

component AutomatonWithLowerCaseName {
  port
    in Integer a;
    
  var int c;
  
  automaton lowerCaseName {
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
