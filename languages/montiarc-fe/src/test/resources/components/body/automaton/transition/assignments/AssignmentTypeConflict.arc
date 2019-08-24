/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/**
 * Invalid model.
 * Cannot assign value '5' to boolean port;
 *
 * @implements [Wor16] AT2: Types of valuations and assignments must match
 *  the type of the assigned input, output, or variable. (p. 105, Lst. 5.24)
 */
component AssignmentTypeConflict {

    port 
        out Boolean b;

    automaton {
      state S;
      initial S;
      S -> S / {b = 5}; // Error
    }
}
