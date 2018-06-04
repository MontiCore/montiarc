package components.body.automaton.transition.guards;

import types.Datatypes.*;

/*
 * Valid model.
 */
component GuardHasComplexExpressionWithCD {

	port
		in java.util.List<MotorCommand> input,
		out String s;

	automaton GuardHasComplexExpressionWithCDAutomaton {
		state A,B;
		initial A;

		A -> B [input.get(0) == MotorCommand.FORWARD]/ {s="Hello World"};
	}
}