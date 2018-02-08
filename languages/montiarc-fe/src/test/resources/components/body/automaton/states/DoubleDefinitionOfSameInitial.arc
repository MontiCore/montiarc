package components.body.automaton.states;

/**
 * Invalid model. Initial states B and C defined twice.
 *
 * @implements [Wor16] AU2: Each state is declared initial at most once. (p. 97, Lst. 5.9)
 */
component DoubleDefinitionOfSameInitial {

	automaton DoubleDefinitionOfSameInitialAutomaton {
		state A,B,C,D;
		state E,F,G,H;

		// B and C are defined as initial twice - 2 Errors.
		initial A,B,C;
		initial B,E,F,C;

		A->B;
		A->C;
		A->D;
		A->E;
		A->F;
		A->G;
		A->H;
	}
}
