package components.body.automaton.states;

/**
 * Invalid model.
 * States B and C defined twice.
 *
 * @implements [Wor16] AU1: The name of each state is unique.
 *  (p. 97. Lst. 5.8)
 */
component StateDefinedMultipleTimes {
	automaton StateDefinedMultipleTimesAutomaton {
		state A, B, C;
		initial A;
		state B, C;
		A->B;
		A->C;
	}

}