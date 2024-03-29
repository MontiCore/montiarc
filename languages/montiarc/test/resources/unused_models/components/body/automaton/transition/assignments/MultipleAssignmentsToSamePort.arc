/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 *
 * @implements [RRW14a] S3ts: In every cycle at most one message per port
 * is sent (partially).
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
