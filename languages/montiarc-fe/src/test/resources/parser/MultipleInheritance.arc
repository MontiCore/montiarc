/* (c) https://github.com/MontiCore/monticore */
//package components.head.inheritance;
package parser;

import contextconditions.valid.Types.*;
import contextconditions.valid.Motor;

/**
 * Invalid model. Multiple inheritance is not allowed
 */
component MultipleInheritance(String s) extends Navi, Subcomponent {
    port
        in Integer speed,
        in MotorCmd command;
}
