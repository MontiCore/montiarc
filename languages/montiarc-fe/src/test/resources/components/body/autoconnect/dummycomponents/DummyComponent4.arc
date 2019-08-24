/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect.dummycomponents;

/*
 * Valid model.
 * Used as subcomponent in autoconnection test
 */
component DummyComponent4 {
  port
    in String dataSthElse,
    in Integer myInt,
    in Boolean bool,
    out String strOut,
    out String sthElse,
    out Integer intOut;
}
