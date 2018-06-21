package components.body.automaton.transition.guards;

/**
 * Invalid model. Cannot assign 'false' to Integer variable or 
 * '255' to a Boolean port.
 *
 * @implements TODO
 */
component MultipleGuardTypeConflics {

	port
		in Integer i,
		in Boolean s,
		in Double d,
		out Integer a;

	automaton {
		state A,B;
		initial A;
		A->B [i == false && s == 255 && d == 2.0];
	}
}