package de.montiarcautomaton.lejos.lib.ultrasonic;

import de.montiarcautomaton.lejos.lib.Datatypes.SensorPort;

component Ultrasonic(SensorPort sensorPort) {
  port
    out Integer distance;

}