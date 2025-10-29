/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

component Calculations {
  port sync in byte    inByte,
       sync in short   inShort,
       sync in int     inInt,
       sync in long    inLong,
       sync in float   inFloat,
       sync in double  inDouble,
       sync in char    inChar,
       sync in boolean inBoolean;

  port sync out byte outByte,
       sync out short   outShort,
       sync out int     outInt,
       sync out long    outLong,
       sync out float   outFloat,
       sync out double  outDouble,
       sync out char    outChar,
       sync out boolean outBoolean;

  automaton {
    initial state S;

    S -> S / {
      // "inter" prefix stands for intermediate.
      byte interByte   = inByte;  // We don't perform calculations for byte, math expression will widen the type to int
      short interShort = inShort; //    Same for short
      int interInt     = -inInt;
      long interLong   = -inLong;
      float interFloat = -inFloat;
      double interDouble   = -inDouble;
      char interChar       = inChar;  // See interByte/Short. Same applies here
      boolean interBoolean = !inBoolean;

      outByte    = interByte;
      outShort   = interShort;
      outInt     = interInt;
      outLong    = interLong;
      outFloat   = interFloat;
      outDouble  = interDouble;
      outChar    = interChar;
      outBoolean = interBoolean;
    }
  }
}
