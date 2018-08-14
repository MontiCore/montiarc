package components.body.automaton.states;

/**
 * Invalid model.
 * State names have to begin with a capital letter.
 *
 * @implements [Wor16] AC8: State names begin with a capital letter.
 *  (p. 101, Lst. 5.18)
 */
component StateNameUppercase {

  automaton A {
    state s, X;
    initial X;
  }
}