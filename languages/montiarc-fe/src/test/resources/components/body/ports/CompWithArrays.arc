package components.body.ports;

import types.*;

/*
 * Valid model.
 */
component CompWithArrays (int[] configIntArray,
                          String[][] configStringArray){

    port
        in int[] intArrayIn,
        in String[][] stringArrayIn,
        in int intNoArrayIn,
        in GenericType<String>[][][] enumArrayIn,
        out short[] shortArrayOut;
}
