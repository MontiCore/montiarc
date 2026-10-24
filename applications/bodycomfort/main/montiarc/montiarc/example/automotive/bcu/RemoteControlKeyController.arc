/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component RemoteControlKeyController(boolean ControlAutomaticPowerWindow, boolean SafetyFunction) {

  port in Trigger rck_but_lock, rck_but_unlock;

  port out Trigger rck_lock, rck_unlock;

  varif (ControlAutomaticPowerWindow) {
    port sync in boolean pw_rm_up, pw_rm_dn;
    port sync out boolean pw_but_up, pw_but_dn;
  }

  varif (SafetyFunction) {
    port sync in boolean door_open;
    port in Trigger time_rck_sf_elapsed;
  }

}
