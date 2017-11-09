package invalid;

component NameConflictVariableAndIO {

	// Multiple declarations of a
	port in Integer a;
	var Integer a;

	// Multiple declarations of b
	var Double b;
	port out Double b;

	// Multiple declarations of c
	port
		in Integer c,
		out Integer c;

	// Multiple declarations of d
	port
		in Integer d,
		in Integer d;

	// Multiple declarations of e
	port
		out Integer e,
		out Integer e;

	//Multiple declarations of f
	var
		Integer f,
		Integer f;

	//Allowed:
	var Integer allowedIdentifier;
	port in Integer anotherIdentifier;

	automaton NameConflictVariableAndIOAutomaton{

		state A;
		initial A;

	}
}