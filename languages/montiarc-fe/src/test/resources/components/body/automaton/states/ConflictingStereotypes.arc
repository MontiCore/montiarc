/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.states;

/**
 * Invalid model.
 * States B and C defined twice with different Stereotypes.
 *
 * @implements [Wor16] AU1: The name of each state is unique.
 *  (p. 97. Lst. 5.8)
 */
component ConflictingStereotypes {
	automaton {
		state A,<<Wrong>> B,C;
		initial A;
		state B, <<Wrong>> C;
		A->B;
		A->C;
	}
}
