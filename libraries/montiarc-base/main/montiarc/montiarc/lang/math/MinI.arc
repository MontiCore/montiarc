/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MinI {
  port <<sync>> in int a, b,
       <<sync>> out int r;

  compute {
    r = Math.min(a, b);
  }
}
