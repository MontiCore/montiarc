package components.body;

/**
 * Invalid model. Various name clashes.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace have to be unique. (p. 59. Lst. 3.31)
 * @implements [Wor16] MU1: The name of each component variable is unique among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
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

  // Multiple declarations of 'string'
  port in String string;
  port out String;

	//Allowed:
	Integer allowedIdentifier;
	port in Integer anotherIdentifier;

	automaton NameConflictVariableAndIOAutomaton{

		state A;
		initial A;

	}

}