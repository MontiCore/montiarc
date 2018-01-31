package component.body.automaton.states;

/**
 * Invalid model. Initial states B and C defined twice.
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
