/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.states;

/*
 * Valid model.
 */
component MultipleStateDeclarations {

	port
		in int i;

	automaton {
		state A;
		state B;

		initial A;
		A -> B [i < 0];
		B -> A [i > 0];
	}
}
