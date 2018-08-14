package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 *
 * @implements No literature reference
 */
component AssignmentWithAlternatives {
    port 
        in int i,
        out int a;

    automaton AutomatonReactionWithAlternatives {
    	state Idle;
    	initial Idle / {a = alt{1,2,3}};
    }
}