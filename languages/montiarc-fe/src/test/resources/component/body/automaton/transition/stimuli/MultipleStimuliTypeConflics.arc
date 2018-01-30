package component.body.automaton.transition.stimuli;

/**
 * Invalid model. Cannot assign 'false' to Integer variable or 
 * '255' to a Boolean port.
 */
component MultipleStimuliTypeConflics {

	port
		in Integer i,
		in Boolean s,
		out Integer a;

	automaton {
		state A,B;
		initial A;
		A->B {i == false, s == 255};
	}
}