/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import types.*;

/*
 * Invalid model.
 *
 * @implements [Hab16] R8: The target port in a connection has to be
 *  compatible to the source port, i.e., the type of the target port is
 *  identical or a supertype of the source port type. (p. 66, lst. 3.43)
 */
component ReferencingComp (int[] configIntArray, String[][] configStringArray){

    port
        in int[] intArrayIn,
        in String[] stringArrayIn,
        in int intNoArrayIn,
        out short[] shortArrayOut;

    component CompWithArrays(configIntArray, configStringArray) ref;

    connect intArrayIn -> ref.intArrayIn;
    connect stringArrayIn -> ref.stringArrayIn;
      //=> port not compatible because of different array dimensions
    connect intNoArrayIn -> ref.intNoArrayIn;
    connect ref.shortArrayOut -> shortArrayOut;
}
