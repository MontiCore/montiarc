/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;

component ExteriorMirror(boolean Heatable, boolean LEDExteriorMirror) {

  port sync in boolean em_but_right, em_but_down, em_but_up, em_but_left,
                       em_pos_left, em_pos_right, em_pos_top, em_pos_bottom;

  port out Direction em_mv;

  varif (Heatable) {
    port sync in boolean em_too_cold;
    port in Trigger time_heating_elapsed;
    port out Trigger heating_on, heating_off;
  }

  varif (LEDExteriorMirror) {
    port out Trigger em_pos_vert_top, em_pos_vert_pend, em_pos_vert_bottom,
                     em_pos_hor_left, em_pos_hor_pend, em_pos_hor_right;
  }

}
