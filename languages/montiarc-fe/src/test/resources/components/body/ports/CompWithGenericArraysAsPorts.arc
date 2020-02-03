/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import types.*;

/*
 * Invalid model.
 * Has arrays of generic types.
 */
component CompWithGenericArraysAsPorts{
    port
        in int[] intArrayIn, // Correct
        in int intNoArrayIn, // Correct
        in List<String>[] configStringListArray, // ERROR
        out List<List<String>[]> innerGenericArray, // ERROR
        in Map<List<String[]>, List<String>[]>[] map, // 2 ERRORs
        out String[][] configStringArray, // Correct
        out List<String[]> configListStringArray, // Correct
        in GenericType<String>[][][] enumArrayIn, // ERROR
        out short[] shortArrayOut; // Correct
}