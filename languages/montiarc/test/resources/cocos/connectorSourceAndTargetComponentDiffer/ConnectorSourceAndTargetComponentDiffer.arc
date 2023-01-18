/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetComponentDiffer;

component ConnectorSourceAndTargetComponentDiffer {

  component Inner {
    port in String sIn;
    port out String sOut;
  }

  Inner i1, i2;

  i1.sOut -> i2.sIn;
  i2.sOut -> i1.sIn;
}
