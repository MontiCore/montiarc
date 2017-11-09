package invalid;

component StateDefinedMultipleTimes {



	automaton StateDefinedMultipleTimesAutomaton{

	//    input int a;
	//    output int b;

		state A, B, C;

		initial A;

		state B, C;

		A->B;
		A->C;
	}

}