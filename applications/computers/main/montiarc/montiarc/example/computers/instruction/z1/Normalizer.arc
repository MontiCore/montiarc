/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

/**
 * Normalizes the mantissa to the range [1, 2), adjusts the exponent
 * accordingly, and packs the result into a 22-bit word. Results that are
 * too small become zero, results that are too large become infinity.
 */
component Normalizer {

  port sync in int exponent,
       sync in boolean sign,
       sync in int mantissa,
       sync in boolean overflow,
       sync out int word;

  compute {
    int e = exponent;
    int m = mantissa;
    if (m != 0) {
      while (m >= 32768) {
        m = m / 2;
        e = e + 1;
      }
      while (m < 16384) {
        m = m * 2;
        e = e - 1;
      }
    }
    int signBit = 0;
    if (sign) {
      signBit = 2097152;
    }
    if (overflow || e > 62) {
      word = signBit + 63 * 16384;
    } else if (m == 0 || e < -63) {
      word = 1048576;
    } else if (e < 0) {
      word = signBit + (e + 128) * 16384 + m - 16384;
    } else {
      word = signBit + e * 16384 + m - 16384;
    }
  }
}
