/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component HumanMachineInterface(boolean AlarmSystem, boolean LEDAlarmSystem,
                              boolean ManualPowerWindow, boolean LEDPowerWindow) {

  port sync in boolean pw_but_mv_dn, pw_but_mv_up,
                       em_but_mv_left, em_but_mv_right, em_but_mv_up, em_but_mv_dn;

  port sync out boolean pw_but_dn, pw_but_up,
                        em_but_right, em_but_down, em_but_up, em_but_left;

  varif (AlarmSystem) {
    port in Trigger activate_as, deactivate_as;
    port out Trigger as_activated, as_deactivated;
  }

  varif (AlarmSystem && LEDAlarmSystem) {
    port in Trigger confirm_alarm;
    port out Trigger as_alarm_was_confirmed;
  }

  varif (ManualPowerWindow && LEDPowerWindow) {
    port in Trigger release_pw_but_dn, release_pw_but_up;
    port out Trigger release_pw_but;
  }

}
