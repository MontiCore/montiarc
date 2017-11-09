package invalid;

import invalid.Test;

component GuardIsNotBoolean {

	port
		in Integer i1, //FULL type
		in Test i3, //Self written JavaType
		out Integer outInt;

	var Integer i2; //QUALIFIED type

	automaton GuardIsNotBooleanAutomaton{


		state A,B;
		initial A;

		A -> B [(i1+i1)*2];
		A -> B [i2];
		A -> B [i3];
	}
}