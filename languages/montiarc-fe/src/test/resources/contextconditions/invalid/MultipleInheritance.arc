package contextconditions.invalid;

import contextconditions.valid.Types.*;
import contextconditions.valid.Motor;

/**
 * Multiple inheritance is not allowed
 */
component MultipleInheritance(String s) extends Navi, Subcomponent {
    port
        in Integer speed,
        in MotorCmd command;
}
