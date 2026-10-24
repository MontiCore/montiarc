/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component FingerProtection {

  port in Trigger finger_detected;

  port sync in boolean pw_but_dn;

  port out Trigger fp_on, fp_off;

}
