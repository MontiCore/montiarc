/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component ExteriorMirror(boolean Heatable, boolean LEDExteriorMirror) {

  port in Direction cmd;

  port sync in boolean pos_left, pos_right, pos_top, pos_bottom;

  port out Direction mv;

  varif (Heatable) {
    port sync in boolean too_cold;
    port in Trigger time_heating_elapsed;
    port out Trigger heating_on, heating_off;
  }

  varif (LEDExteriorMirror) {
    port out Trigger pos_vert_top, pos_vert_pend, pos_vert_bottom,
                     pos_hor_left, pos_hor_pend, pos_hor_right;
  }

}
