/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component LEDAutomaticPowerWindow(boolean CentralLockingSystem) {

  port in AutoMove pw_auto_mv;

  port sync out boolean led_pw_up, led_pw_dn;

  varif (CentralLockingSystem) {
    port in Trigger cls_lock, cls_unlock;
    port sync out boolean led_pw_cls_up;
  }

}
