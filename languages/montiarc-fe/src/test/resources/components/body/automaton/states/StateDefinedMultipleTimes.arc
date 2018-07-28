package components.body.automaton.states;

/**
 * Invalid model.
 * States B and C defined twice.
 *
 * @implements No literature reference
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