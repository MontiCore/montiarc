/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

/**
 * Converts a decimal number entered at the keyboard into a 22-bit word.
 * The mantissa is truncated to 14 bits.
 */
component Keyboard {

  port sync in double value,
       sync out int word;

  compute {
    double v = value;
    int signBit = 0;
    if (v < 0) {
      v = -v;
      signBit = 2097152;
    }
    int e = 0;
    if (v != 0) {
      while (v >= 2 && e < 63) {
        v = v / 2;
        e = e + 1;
      }
      while (1 > v && e > -64) {
        v = v * 2;
        e = e - 1;
      }
    }
    int m = 0;
    double fraction = v - 1;
    for (int i = 0; i < 14; i++) {
      fraction = fraction * 2;
      m = m * 2;
      if (fraction >= 1) {
        m = m + 1;
        fraction = fraction - 1;
      }
    }
    if (e > 62) {
      word = signBit + 63 * 16384;
    } else if (v == 0 || e < -63) {
      word = 1048576;
    } else if (e < 0) {
      word = signBit + (e + 128) * 16384 + m;
    } else {
      word = signBit + e * 16384 + m;
    }
  }
}
