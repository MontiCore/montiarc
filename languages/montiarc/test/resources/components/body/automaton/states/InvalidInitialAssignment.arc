/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.states;

/**
 * Invalid model. Cannot assign 'false' to Integer variable or 
 * '255' to a Boolean port.
 *
 * @implements [Wor16] AT2: Types of valuations and assignments must match
 *  the type of the assigned input, output, or variable. (p. 105, Lst. 5.24)
 */
component InvalidInitialAssignment {

	port
		in String e,
		out Integer i,
		out Boolean s;

	Integer v;

	automaton {
		state A,B;
		initial A /{v = false, s = 255 }; // 2 errors
		A->B;
	}
}
