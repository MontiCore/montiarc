/* (c) https://github.com/MontiCore/monticore */
package light;

/*
 * Valid model.
 */
component Arbiter {
  port in SwitchStatus switchStatus,
       in OnOffRequest onOffRequest,
       in BlinkRequest blinkRequest,
       out OnOffCmd onOffCmd;
}