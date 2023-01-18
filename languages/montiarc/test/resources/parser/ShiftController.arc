/* (c) https://github.com/MontiCore/monticore */
package parser;

/**
 * Valid Model.
 * Example component from paper "Retrofitting Controlled Dynamic
 *                               Reconfiguration into the Architecture
 *                               Description Language MontiArcAutomaton"
 * Syntax differs slightly.
 *
 */
component ShiftController {
  port in TOM tom,
      out GSCmd cmd;

  ManShiftCtrl manual;
  AutoShiftCtrl auto;
  SCSensors scs;

  mode Idle {}

  mode Manumatic { /* ... */ }

  mode Auto { /* ... */ }

  mode Sport, Kickdown {
    SportShiftCtrl sport;

    scs.rpm -> sport.rpm;
    scs.tpi -> sport.tpi;
    scs.vi -> sport.vi;
    sport.cmd -> cmd;
  }

  mode automaton {
    initial Idle;
    Idle -> Auto [tom == DRIVE];
    Auto -> Kickdown [scs.tpi > 90 && tom == DRIVE];
    Kickdown -> Auto [scs.tpi < 90 && tom == DRIVE];
    // further transitions
  }
}
