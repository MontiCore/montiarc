package component.body.automaton;

/**
 * Invalid model. Components may contain one behavior automaton at most
 */
component MultipleAutomata {
  port
    in Integer a;

  int c;

  automaton InvalidAutomatonBehaviorImpl {
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
  
  automaton DuplicatedAutomatonImpl {  
  	state Beginn;
    initial Beginn;

    Beginn;
  }

  automaton AnotherAutomatonImpl {
  	state Anfang;
    initial Anfang;

    Anfang -> Anfang [a > 2];
  }
}
