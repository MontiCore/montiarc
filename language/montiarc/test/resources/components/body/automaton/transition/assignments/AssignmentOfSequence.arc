/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/**
 * Invalid model. Reads and writes sequences from/to variables.
 *
 * @implements [RRW14a] T5: Sequences of values cannot be read from or assigned to variables.
 */

 component AssignmentOfSequence{

    Integer buffer;

    automaton{
        state S;
        initial S;

        S  / {buffer = [1,1]};
    }
 }
