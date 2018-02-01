package components.body.automaton.states;

/**
 * Invalid model. States B and C defined twice.
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