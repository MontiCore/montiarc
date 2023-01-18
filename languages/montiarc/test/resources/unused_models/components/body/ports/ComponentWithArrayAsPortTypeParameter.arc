/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import types.GenericType;
import types.CType;

/**
* Valid model.
*/
component ComponentWithArrayAsPortTypeParameter {

    port
        in GenericType<byte[]> genIn,
        in GenericType<CType[]> getCTypeIn,
        out GenericType<byte[]> genOut,
        out GenericType<CType[]> getCTypeOut;

}
