package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 *
 * @implements [RRW14a] S3ts: In every cycle at most one message per port is sent.
 */
component OneAssignmentPerCycle {

    port
        in int i,
        out int o,
        out int x,
        out int y;

    int v;

    automaton OneAssignmentPerCycle{
        state S;
        initial S;

        S [v == 1] / { v=2,
                              y=1,
                              v=3,
                              o=3,
                              o=3,
                              x=1,
                              x=5};

        S / { x = [5,2,4,3,1]};
    }
}