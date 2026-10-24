/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component ManualPowerWindow(boolean CentralLockingSystem) {

  port sync in boolean pw_but_dn, pw_but_up,
                       pw_pos_up, pw_pos_dn;

  port in Trigger fp_on, fp_off;

  port out Move pw_mv;

  varif (CentralLockingSystem) {
    port in Trigger cls_lock, cls_unlock;
  }

}
