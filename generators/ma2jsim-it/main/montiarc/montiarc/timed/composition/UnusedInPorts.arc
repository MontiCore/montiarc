/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

component UnusedInPorts {
  port in boolean i1, i2;
  port out boolean o1, o2;

  component Inner sub {
    port in boolean i;
    port out boolean o1, o2;
  }

  i1 -> sub.i;
  sub.o1 -> o1;
  sub.o2 -> o2;

  // i2 not connected
}