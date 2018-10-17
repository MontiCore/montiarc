package components.body.automaton.transition.guards;

import types.JavaNumberType;

/*
 * Valid model.
 */
component GuardIsBoolean {

	port
    in JavaNumberType input,
		out List<String> x;

  List<String> y;
	automaton GuardIsBooleanAutomaton {
		state A,B;
		initial A;

		A -> B [input.get()==0] / {y = new ArrayList<String>(), x = new ArrayList<String>()};
		B -> A [true] / {call y.add("bu")};
	}
}