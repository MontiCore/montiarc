/* (c) https://github.com/MontiCore/monticore */
package light;

/*
 * Valid model.
 */
component AlarmCheck(Integer timer) {
  port in AlarmStatus alarmStatus,
       in BlinkRequest blinkRequest;
}