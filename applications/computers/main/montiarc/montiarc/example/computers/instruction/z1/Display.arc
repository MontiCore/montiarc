/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

/**
 * Converts a 22-bit word into a decimal number and shows it. The display
 * keeps showing the last number until the next one is shown.
 */
component Display {

  port sync in boolean show,
       sync in int word,
       sync out double value;

  double shown = 0.0;

  compute {
    if (show) {
      int e = (word / 16384) % 128;
      if (e >= 64) {
        e = e - 128;
      }
      double v = 1.0 + (word % 16384) / 16384.0;
      if (e == -64) {
        v = 0.0;
      } else if (e == 63) {
        v = 1.0 / 0.0;
      } else {
        while (e > 0) {
          v = v * 2;
          e = e - 1;
        }
        while (e < 0) {
          v = v / 2;
          e = e + 1;
        }
      }
      if (word >= 2097152) {
        v = -v;
      }
      shown = v;
    }
    value = shown;
  }
}
