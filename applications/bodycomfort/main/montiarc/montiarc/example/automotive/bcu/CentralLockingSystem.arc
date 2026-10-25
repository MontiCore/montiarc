/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component CentralLockingSystem(boolean RemoteControlKey, boolean AutomaticLocking) {

  port in Trigger key_pos_lock, key_pos_unlock;

  port out Trigger lock, unlock;

  varif (RemoteControlKey) {
    port in Trigger rck_lock, rck_unlock;
  }

  varif (AutomaticLocking) {
    port sync in boolean car_drives, door_open;
    port out Trigger car_lock, car_unlock;
  }

}
