/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component LEDManualPowerWindow {

  port in Move pw_mv;

  port in Trigger release_pw_but;

  port sync out boolean led_pw_up, led_pw_dn;

}
