package invalid;

component InitialValueFit {

//	port
//		in Integer a=5,
//		in Integer b="Wrong",
//		out Integer d="Wrong",
//		out Integer e=5;

	var Integer c="Wrong";
	var Integer f=2;

	automaton InitialValueFitAutomaton {

		state Idle;
		initial Idle;
	}
}