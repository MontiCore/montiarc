package d;

import b.*;

component CompWithArrays (int[] configIntArray, String[][] configStringArray){

    port
        in int[] intArrayIn,
        in String[][] stringArrayIn,
        in int intNoArrayIn,
        in GenericClassType<String>[][][] enumArrayIn,
        out short[] shortArrayOut;
        
}