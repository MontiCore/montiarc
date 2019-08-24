/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.lejos.lib.timer;

import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerCmd;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerSignal;

component Timer(Integer delay) {
  port
    in TimerCmd cmd,
    out TimerSignal signal;

}
