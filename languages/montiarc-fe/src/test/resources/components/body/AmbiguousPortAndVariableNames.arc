package components.body;

/**
 * Invalid model. Various name clashes.
 */
component AmbiguousPortAndVariableNames {

	// Multiple declarations of a
	port in Integer a;
	Integer a;

	// Multiple declarations of b
	Double b;
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

	// Multiple declarations of f
	Integer f, f;

	//Allowed:
	Integer allowedIdentifier;
	port in Integer anotherIdentifier;

	automaton NameConflictVariableAndIOAutomaton{

		state A;
		initial A;

	}

}