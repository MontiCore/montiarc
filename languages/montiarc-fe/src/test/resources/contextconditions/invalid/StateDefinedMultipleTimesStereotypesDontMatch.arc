package invalid;

component StateDefinedMultipleTimesStereotypesDontMatch {

	automaton StateDefinedMultipleTimesStereotypesDontMatch{

		state A,<<Wrong>> B,C;

		initial A;

		state B, <<Wrong>> C;

		A->B;
		A->C;
	}
}