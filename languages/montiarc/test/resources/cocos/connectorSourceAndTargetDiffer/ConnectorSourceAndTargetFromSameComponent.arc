/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetDiffer;

component ConnectorSourceAndTargetFromSameComponent {

  component Inner {
    port in String sIn;
    port out String sOut;
  }

  Inner i;

  i.sOut -> i.sIn;
}
