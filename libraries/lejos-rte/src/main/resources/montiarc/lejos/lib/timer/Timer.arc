/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.timer;

import montiarc.lejos.lib.timer.TimerCmd;
import montiarc.lejos.lib.timer.TimerSignal;

import java.lang.Integer;

component Timer(Integer delay) {
  port
    in TimerCmd cmd,
    out TimerSignal signal;

}
