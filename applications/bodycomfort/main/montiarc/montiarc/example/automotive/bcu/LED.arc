/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

/**
 * Status LED that is switched on and off by separate events.
 */
component LED {

  port in Trigger turn_on, turn_off;

  port sync out boolean led;

}
