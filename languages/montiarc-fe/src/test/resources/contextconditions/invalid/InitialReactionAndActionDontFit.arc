package invalid;

component InitialReactionAndActionDontFit {

	port
		in Input e,
		out Integer i,
		out Boolean s;

	var Integer v;

	automaton InitialReactionAndActionDontFit{

		state A,B;

		initial A /{v = false, s = 255 };

		A->B;

	}
}