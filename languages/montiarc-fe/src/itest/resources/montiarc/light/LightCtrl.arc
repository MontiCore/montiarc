/* (c) https://github.com/MontiCore/monticore */
package light;

import light.AlarmCheck;
import light.Arbiter;
import light.Datatypes.*;
import light.DoorEval;

/*
 * Valid model.
 */
component LightCtrl {
  autoconnect port;

  Arbiter arbiter;
  DoorEval doorEval(3);
  AlarmCheck alarmCheck(5);

  port in SwitchStatus switchStatus,
       in DoorStatus doorStatus,
       in AlarmStatus alarmStatus,
       out OnOffCmd onOffCmd;
}