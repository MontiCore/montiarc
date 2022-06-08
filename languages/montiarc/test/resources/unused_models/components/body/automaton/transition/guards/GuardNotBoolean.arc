/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.guards;


/*
 * Invalid model.
 *
 * @implements [Wor16] AT1: Guard expressions evaluate to a Boolean truth
 *  value. (p.105, Lst. 5.23)
 */
component GuardNotBoolean {

	port
		in Integer i1, //FULL type
		in Test i3, //Self written JavaType
		out Integer outInt;

	Integer i2; //QUALIFIED type

	automaton GuardIsNotBooleanAutomaton {
		state A,B;
		initial A;

		A -> B [(i1+i1)*2];
		A -> B [i2];
		A -> B [i3];
	}
}
