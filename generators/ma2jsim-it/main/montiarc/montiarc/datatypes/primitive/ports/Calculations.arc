/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

component Calculations {
  port in byte    inByte,
       in short   inShort,
       in int     inInt,
       in long    inLong,
       in float   inFloat,
       in double  inDouble,
       in char    inChar,
       in boolean inBoolean;

  port out byte outByte,
       out short   outShort,
       out int     outInt,
       out long    outLong,
       out float   outFloat,
       out double  outDouble,
       out char    outChar,
       out boolean outBoolean;

  <<sync>> automaton {
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
    };
  }
}
