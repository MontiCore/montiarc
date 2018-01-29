package component.body.automaton.states;

/**
 * Invalid model. States B and C defined twice with different
 * Stereotypes.
 */
component ConflictingStereotypes {
	automaton {
		state A,<<Wrong>> B,C;
		initial A;
		state B, <<Wrong>> C;
		A->B;
		A->C;
	}
}