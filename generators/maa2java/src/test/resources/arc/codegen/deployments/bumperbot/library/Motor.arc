package bumperbot.library;

import bumperbot.library.Types.*;

//abstract 
component Motor(String name, String typ = "BACK") {
    port
        in MotorCmd speed;
}
