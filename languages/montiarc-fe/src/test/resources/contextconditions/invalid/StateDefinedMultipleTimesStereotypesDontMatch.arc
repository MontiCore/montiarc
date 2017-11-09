package invalid;

component StateDefinedMultipleTimesStereotypesDontMatch {

	automaton StateDefinedMultipleTimesStereotypesDontMatch{

//		input int a;
//		output int b;

		state A,<<Wrong>> B,C;

		initial A;

		state B, <<Wrong>> C;

		A->B;
		A->C;
	}
}