package invalid;

component VariableMoreThanOnceConflict{

	var Integer a;
	var Double a;

	automaton VariableMoreThanOnceConflict {

		state S;
		initial S;
	}
}