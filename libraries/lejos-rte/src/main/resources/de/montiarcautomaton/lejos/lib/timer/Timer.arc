/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.timer;

import montiarc.lejos.lib.timer.Datatypes.TimerCmd;
import montiarc.lejos.lib.timer.Datatypes.TimerSignal;

component Timer(Integer delay) {
  port
    in TimerCmd cmd,
    out TimerSignal signal;

}
