/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component AlarmSystem(boolean ControlAlarmSystem, boolean InteriorMonitoring) {

  port in Trigger key_pos_lock, key_pos_unlock,
                  as_activated, as_deactivated,
                  as_alarm_detected, time_alarm_elapsed;

  port out Trigger as_active_on, as_active_off, as_alarm_on, as_alarm_off;

  port out Trigger as_alarm_was_detected;

  varif (ControlAlarmSystem) {
    port in Trigger rck_lock, rck_unlock;
  }

  varif (InteriorMonitoring) {
    port in Trigger im_alarm_detected;
    port out Trigger as_im_alarm_on, as_im_alarm_off;
  }

}
