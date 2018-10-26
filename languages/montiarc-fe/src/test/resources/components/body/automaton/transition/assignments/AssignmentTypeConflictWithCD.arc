package components.body.automaton.transition.assignments;

import types.CDTestTypes.*;

/**
 * Invalid model.
 * Cannot assign value '5' to Car port;
 *
 * @implements [Wor16] AT2: Types of valuations and assignments must match
 *  the type of the assigned input, output, or variable. (p. 105, Lst. 5.24)
 */
component AssignmentTypeConflictWithCD {

    port 
        out int iOut;

    TypeWithFields tf;
    
    automaton {
      state S;
      initial S / {iOut = tf.t};
    }
}