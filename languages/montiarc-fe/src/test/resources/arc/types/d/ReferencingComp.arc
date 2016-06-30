package d;

import b.*;

component ReferencingComp (int[] configIntArray, String[][] configStringArray){
    
    port
        in int[] intArrayIn,
        in String[] stringArrayIn,
        in int intNoArrayIn,
        in GenericClassType<String>[][][] enumArrayIn,
        out short[] shortArrayOut;
        
    component CompWithArrays(configIntArray, configStringArray) ref;
    
    connect intArrayIn -> ref.intArrayIn;
    connect stringArrayIn -> ref.stringArrayIn; //=> ports not compatible because of different array dimensions
    connect intNoArrayIn -> ref.intNoArrayIn;
    connect enumArrayIn -> ref.enumArrayIn;
    connect ref.shortArrayOut -> shortArrayOut;
}