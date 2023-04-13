/* (c) https://github.com/MontiCore/monticore */
package composition;

component DelayedDecomposed {
  port in Integer iIn;

  DelayedSorter sorter; // sort inputs into >=0 and <0 (2 outputs)
  Counter gtEq0; // count outputs that were >=0 (output port not forwarded)
  Counter lt0; // count outputs that were <0 (output port not forwarded)

  iIn -> sorter.iIn;
  sorter.gtEq0 -> gtEq0.iIn;
  sorter.lt0 -> lt0.iIn;

}
