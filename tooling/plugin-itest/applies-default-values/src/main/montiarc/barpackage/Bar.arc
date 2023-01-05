/* (c) https://github.com/MontiCore/monticore */
package barpackage;

component Bar {
  port
    <<sync>> in int inPort,
    <<sync>> out int outPort;

  compute {
    outPort = inPort + 1;
  }

}