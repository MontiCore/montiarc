package components.body.automaton;

/**
 * Invalid model. Components may contain one behavior automaton at most.
 * @implements [Wor16] MU2: Each atomic component contains at most one behavior model. (p. 55. Lst. 4.6)
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
