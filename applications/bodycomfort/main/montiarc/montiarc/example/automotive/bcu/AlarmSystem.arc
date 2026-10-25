/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component AlarmSystem(boolean ControlAlarmSystem, boolean InteriorMonitoring) {

  port in Trigger key_pos_lock, key_pos_unlock,
                  activated, deactivated,
                  alarm_detected, time_alarm_elapsed;

  port out Trigger active_on, active_off, alarm_on, alarm_off;

  port out Trigger alarm_was_detected;

  varif (ControlAlarmSystem) {
    port in Trigger rck_lock, rck_unlock;
  }

  varif (InteriorMonitoring) {
    port in Trigger im_alarm_detected;
    port out Trigger im_alarm_on, im_alarm_off;
  }

}
