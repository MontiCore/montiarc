package contextconditions.valid;

import contextconditions.valid.Types.*; 
 
component Motor(String name, String typ = "BACK") {
    port
        in Integer speed,
        in MotorCmd command;
}
