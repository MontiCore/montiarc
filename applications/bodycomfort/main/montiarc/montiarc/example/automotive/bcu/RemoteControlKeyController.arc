/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component RemoteControlKeyController(boolean ControlAutomaticPowerWindow, boolean SafetyFunction) {

  port in Trigger but_lock, but_unlock;

  port out Trigger lock, unlock;

  varif (ControlAutomaticPowerWindow) {
    port sync in boolean pw_rm_up, pw_rm_dn;
    port sync out boolean pw_but_up, pw_but_dn;
  }

  varif (SafetyFunction) {
    port sync in boolean door_open;
    port in Trigger time_sf_elapsed;
  }

}
