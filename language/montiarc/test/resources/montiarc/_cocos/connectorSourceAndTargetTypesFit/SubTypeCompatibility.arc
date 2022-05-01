/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit;

/*
 * Valid model. (Given that Student is a subtype of Person and both are resolvable)
 */
component SubTypeCompatibility {
  port in Student sIn;
  port out Person pOut;

  component Sub {
    port in Person pIn;
    port out Student sOut;
  }

  Sub sub;

  sIn -> sub.pIn;
  sub.sOut -> pOut;
}