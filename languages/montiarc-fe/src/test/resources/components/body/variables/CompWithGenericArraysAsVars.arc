package components.body.variables;

import types.*;

/*
 * Invalid model.
 * Has arrays of generic types.
 * @implements No literature reference
 */
component CompWithGenericArraysAsVars{
    List<String>[] configStringListArray; // Error
    List<String>[][] configStringListArray2; // Error
    List<List<String>[]> innerGenericArray; // ERROR
    Map<List<String[]>, List<String>[]>[] map; // 2 ERRORs
    String[][] configStringArray; // Correct
    List<String[]> configListStringArray; // Correct
}
