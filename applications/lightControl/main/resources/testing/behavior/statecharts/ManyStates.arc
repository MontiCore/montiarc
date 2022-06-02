/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

/**
 * builds up on "TwoStates" and "TransitionGuard"
 * adds a many states
 */
component ManyStates {

  port in int gas;
  port out String s;

  automaton {
    initial state Parking;
    state Accelerating;
    state Cruising;
    state LossOfControl;
    state Braking;
    state Breaking;

    Parking -> Accelerating [gas > 20] / {s = "acc";};
    Accelerating -> Cruising [gas < 20] / {s = "cru";};
    Accelerating -> Breaking [gas >= 100] / {s = "bre";};
    Breaking -> LossOfControl / {s = "loc";};
    LossOfControl -> Parking [gas == 0] / {s="par";};
    Cruising -> Accelerating [gas > 20] / {s = "acc";};
    Cruising -> Braking [gas < 0] / {s = "bra";};
    Braking -> Parking [gas == 0] / {s = "par";};
    Braking -> Accelerating [gas > 20] / {s = "acc";};
    Braking -> Breaking [gas < -99] / {s = "bre";};

    // default
    Parking -> Parking / {s="par";};
    Accelerating -> Accelerating / {s = "acc";};
    Cruising -> Cruising / {s = "cru";};
    LossOfControl -> LossOfControl / {s = "loc";};
    Breaking -> Breaking / {s = "bre";};
    Braking -> Braking / {s = "bra";};
  }
}