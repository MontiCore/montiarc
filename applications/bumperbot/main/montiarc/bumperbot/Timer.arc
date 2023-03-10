/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Timer(Integer delay) {
  port
    <<sync>> in bumperbot.Datatypes.TimerCmd cmd,
    <<sync, delayed>> out bumperbot.Datatypes.TimerSignal signal;

}
