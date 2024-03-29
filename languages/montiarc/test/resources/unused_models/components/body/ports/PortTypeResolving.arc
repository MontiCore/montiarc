/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import types.Datatypes.*;

/**
 * Valid model.
 */
component PortTypeResolving(String name, String typ = "BACK") {
    port
        in Integer speed,
        in MotorCommand command;
}
