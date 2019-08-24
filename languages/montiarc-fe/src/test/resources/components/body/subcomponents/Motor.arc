/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import types.Datatypes.*; 

/**
 * Valid model. Used by Navi.
 */
component Motor(String name, String typ = "BACK") {
    port
        in Integer speed,
        in MotorCommand command;
}
