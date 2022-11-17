/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component DivideI {
  port <<sync>> in int a, b,
       <<sync>> out int r;
  port <<sync>> out boolean of;

  compute {
    r = a / b;
    of = a == Integer.MAX_VALUE && b == -1;
  }
}
