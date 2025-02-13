/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Timer(Integer delay) {
  port
    sync in bumperbot.Datatypes.TimerCmd cmd,
    <<delayed>> sync out bumperbot.Datatypes.TimerSignal signal;

}
