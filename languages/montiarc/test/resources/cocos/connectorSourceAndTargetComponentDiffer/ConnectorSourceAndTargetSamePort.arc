/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetComponentDiffer;

component ConnectorSourceAndTargetSamePort {

  component Inner {
      port out String sOut;
    }

    Inner i;

    i.sOut -> i.sOut;
}
