/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

component UnusedOutPorts {
  port in boolean i1, i2;
  port out boolean o1, o2;

  component Inner sub {
    port in boolean i1, i2;
    port out boolean o;
  }

  i1 -> sub.i1;
  i1 -> sub.i2;
  sub.o -> o1;

  // i2, o2 not connected
}