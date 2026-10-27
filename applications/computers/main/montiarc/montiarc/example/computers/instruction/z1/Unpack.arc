/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

/**
 * Splits a 22-bit word into sign, exponent, and mantissa. The mantissa
 * includes the hidden leading one, i.e., 16384 represents 1.0. Zero has the
 * mantissa 0.
 */
component Unpack {

  port sync in int word,
       sync out boolean sign,
       sync out int exponent,
       sync out int mantissa;

  compute {
    int e = (word / 16384) % 128;
    if (e >= 64) {
      e = e - 128;
    }
    sign = word >= 2097152;
    exponent = e;
    if (e == -64) {
      mantissa = 0;
    } else {
      mantissa = 16384 + word % 16384;
    }
  }
}
