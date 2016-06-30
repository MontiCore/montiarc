package bumperbot.library;

import bumperbot.library.types.*; 
 
component Motor(String name, String typ = "BACK") {
    port
        in Integer speed,
        in MotorCmd command;
}
