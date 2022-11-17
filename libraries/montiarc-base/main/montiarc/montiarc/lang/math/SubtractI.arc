/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component SubtractI {
  port <<sync>> in int a, b,
       <<sync>> out int r;
  port <<sync>> out boolean of;

  compute {
    int _r = a - b;
    r = _r;
    of = 0 > ((a ^ b) & (a ^ _r));
  }
}
