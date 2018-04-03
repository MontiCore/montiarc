package components.body.automaton.transition.guards;

import components.body.automaton.transition.guards.Number;

component GuardIsBoolean {

	port
		in Number inputs;

	automaton GuardIsNotBooleanAutomaton {
		state A,B;
		initial A;

		A -> B [inputs.get()==0];
	}
}