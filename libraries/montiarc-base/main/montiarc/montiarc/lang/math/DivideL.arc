/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component DivideL {
  port <<sync>> in long a, b,
       <<sync>> out long r;
  port <<sync>> out boolean of;

  compute {
    r = a / b;
    of = a == Long.MAX_VALUE && b == -1;
  }
}
