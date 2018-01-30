package component.body.automaton.states;

/**
 * Invalid model. Cannot assign 'false' to Integer variable or 
 * '255' to a Boolean port.
 */
component InvalidInitialAssignment {

	port
		in String e,
		out Integer i,
		out Boolean s;

	var Integer v;

	automaton {
		state A,B;
		initial A /{v = false, s = 255 }; // 2 errors
		A->B;
	}
}