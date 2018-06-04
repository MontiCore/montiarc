package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 *
 * @implements TODO
 */
component MultipleAssignmentsToSamePort {

    port 
        in int i,
        out int o,
        out int x,
        out int y;

    int v;

    automaton MultipleAssignmentsToSamePort {
        state S;
        initial S;
    
        S [v == 1] / { v=2, 
                              y=1, 
                              v=3, 
                              o=3, 
                              o=4, 
                              x=1, 
                              x=5};
    }
}