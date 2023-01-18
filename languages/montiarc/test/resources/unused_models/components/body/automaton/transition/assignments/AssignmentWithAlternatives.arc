/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
 * Invalid model.
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
