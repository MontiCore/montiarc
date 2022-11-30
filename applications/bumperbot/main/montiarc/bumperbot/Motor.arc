/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Motor {
  port
    <<sync>> in MotorCmd cmd,
    <<sync>> in Integer speed;

}
