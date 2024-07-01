/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

component UnusedPorts {
  port in boolean i1, i2;
  port out boolean o1, o2;

  component Inner sub {
    port in boolean i;
    port out boolean o;
  }

  i1 -> sub.i;
  sub.o -> o1;

  // i2 not connected
  // o2 not connected
}