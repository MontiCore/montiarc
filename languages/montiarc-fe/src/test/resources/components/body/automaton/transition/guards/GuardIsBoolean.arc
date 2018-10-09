package components.body.automaton.transition.guards;

import types.JavaNumberType;

/*
 * Valid model.
 */
component GuardIsBoolean {

	port
		in JavaNumberType input;

	automaton GuardIsNotBooleanAutomaton {
		state A,B;
		initial A;

		A -> B [input.get()==0];
	}
}