/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component SubtractL {
  port <<sync>> in long a, b,
       <<sync>> out long r;
  port <<sync>> out boolean of;

  compute {
    long _r = a - b;
    r = _r;
    of = 0 > ((a ^ b) & (a ^ _r));
  }
}
