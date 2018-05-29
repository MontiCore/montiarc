package components.body.automaton.transition.stimuli;

/**
 * Invalid model. Cannot assign 'false' to Integer variable or 
 * '255' to a Boolean port.
 *
 * @implements TODO
 */
component MultipleStimuliTypeConflics {

	port
		in Integer i,
		in Boolean s,
		out Integer a;

	automaton {
		state A,B;
		initial A;
		A->B [i == false && s == 255];
	}
}