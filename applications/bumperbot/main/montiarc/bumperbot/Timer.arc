/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Timer(Integer delay) {
  port
    <<sync>> in TimerCmd cmd,
    <<causalsync>> out TimerSignal signal;

}
