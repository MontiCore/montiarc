package invalid;

component InitialValueFit {

	port
		in Integer a,
		in Integer b,
		out Integer d,
		out Integer e;

	var Integer c;
	var Integer f;

	automaton InitialValueFitAutomaton {

		state Idle;
		initial Idle / {c="Wrong",f=2,d="Wrong",e=5,g="Not declared"};
	}

}