package invalid;

component StateDefinedMultipleTimes {


	automaton StateDefinedMultipleTimesAutomaton{

		state A, B, C;

		initial A;

		state B, C;

		A->B;
		A->C;
	}

}