/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Timer(Integer delay) {
  port
    <<sync>> in TimerCmd cmd,
    <<sync, delayed>> out TimerSignal signal;

}
