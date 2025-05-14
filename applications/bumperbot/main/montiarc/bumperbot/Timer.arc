/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Timer(Integer delay) {
  port
    sync in bumperbot.Datatypes.TimerCmd cmd,
    sync out bumperbot.Datatypes.TimerSignal signal;

  <<delayed>> compute {}
}
