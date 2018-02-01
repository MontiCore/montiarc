package components.body.subcomponents;

import valid.Datatypes.*; 

/**
 * Valid model. Used by Navi.
 */
component Motor(String name, String typ = "BACK") {
    port
        in Integer speed,
        in MotorCmd command;
}
