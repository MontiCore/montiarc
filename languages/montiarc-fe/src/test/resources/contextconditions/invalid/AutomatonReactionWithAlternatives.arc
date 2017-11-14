package contextconditions.invalid;


component AutomatonReactionWithAlternatives{

port in int i,
     out int a;

automaton AutomatonReactionWithAlternatives {



	state Idle;
	initial Idle / {a = alt{1,2,3}};

}

}