/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MaxI {
  port <<sync>> in int a, b,
       <<sync>> out int r;

  compute {
    r = Math.max(a, b);
  }
}
