package invalid;

component StimulusAndEventDontFit {

	port
		in Integer i,
		in Boolean s,
		out Integer a;

	automaton StimulusAndEventDontFit{

		state A,B;

		initial A;

		A->B {i == false, s == 255};
	}
}