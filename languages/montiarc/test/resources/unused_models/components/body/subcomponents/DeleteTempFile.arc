/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.timing.delays.ConstantDelay;

/**
 * Valid model. Used by UsingComplexParams
 */
component DeleteTempFile(int deleteAfter) {

    timing instant;

  port
    in String fileName;

    component ConstantDelay<String>(deleteAfter) delay;

    component InternDeleteTempFile del {
        port
            in String fileName;
    }

    connect fileName -> delay.portIn;
    connect delay.portOut -> del.fileName;
}
