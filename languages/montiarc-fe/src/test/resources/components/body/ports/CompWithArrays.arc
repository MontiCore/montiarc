package components.body.ports;

/*
 * Valid model.
 */
component CompWithArrays (int[] configIntArray,
                          String[][] configStringArray){

    port
        in int[] intArrayIn,
        in String[][] stringArrayIn,
        in int intNoArrayIn,
        out short[] shortArrayOut;
}
