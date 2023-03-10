/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component Motor {
  port
    <<sync>> in bumperbot.Datatypes.MotorCmd cmd,
    <<sync>> in Integer speed;

}
