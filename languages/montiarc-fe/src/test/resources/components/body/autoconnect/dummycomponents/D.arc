package components.body.autoconnect.dummycomponents;

/*
 * Valid model.
 * Used as subcomponent in autoconnection test
 * TODO Add test or remove or refactor
 */
component D {
  port
    in String dataSthElse,
    in Integer myInt,
    in Boolean bool,
    out String strOut,
    out String sthElse,
    out Integer intOut;
}