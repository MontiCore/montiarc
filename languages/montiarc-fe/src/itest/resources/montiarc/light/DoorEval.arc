/* (c) https://github.com/MontiCore/monticore */
package light;

/*
 * Valid model.
 */
component DoorEval(Integer timer) {
  port in DoorStatus doorStatus,
       in SwitchStatus switchStatus,
       out OnOffRequest onOffRequest;
}