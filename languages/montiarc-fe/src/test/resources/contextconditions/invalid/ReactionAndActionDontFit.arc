package invalid;

component ReactionAndActionDontFit {

	port
		in Integer i,
		out Integer a;

	automaton ReactionAndActionDontFitAutomaton{

		state A;
		initial A;

		A / {a = false};
	}
}