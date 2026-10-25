/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component ManualPowerWindow(boolean CentralLockingSystem) {

  port sync in boolean but_dn, but_up,
                       pos_up, pos_dn;

  port in Trigger fp_on, fp_off;

  port out Move mv;

  varif (CentralLockingSystem) {
    port in Trigger cls_lock, cls_unlock;
  }

}
