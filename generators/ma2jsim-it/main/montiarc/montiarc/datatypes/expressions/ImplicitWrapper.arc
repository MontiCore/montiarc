/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.expressions;


component ImplicitWrapper {

  automaton {
    initial state S;
    S -> S / {
      // Explicitly uninitialized local variables
      boolean aBoolean;
      char aChar;
      byte aByte;
      short aShort;
      int anInt;
      long aLong;
      float aFloat;
      double aDouble;

      Boolean aWBoolean;
      Character aWChar;
      Byte aWByte;
      Short aWShort;
      Integer aWInt;
      Long aWLong;
      Float aWFloat;
      Double aWDouble;

      Object anObject;
      Number aNumber;

      Comparable<Boolean> aCpaBoolean;
      Comparable<Character> aCpaChar;
      Comparable<Integer> aCpaInt;
      Comparable<Long> aCpaLong;
      Comparable<Float> aCpaFloat;
      Comparable<Double> aCpaDouble;


      aWBoolean = true; // box boolean in Boolean (true literal)
      aWBoolean = false; // box boolean in Boolean (false literal)
      aWBoolean = aBoolean; // box boolean in Boolean (boolean variable)
      aWBoolean = aWBoolean; // assign Boolean to Boolean (variable)
      aWChar = 'a'; // box char in Character (literal)
      //aWChar = 0; // cast int to char, box char in Character (min value)
      //aWChar = 65535; //  cast int to char, box char in Character (max value)
      aWChar = aChar; // box char in Character (variable)
      aWChar = aWChar; // expected Character, provided Character (variable)
      //aWByte = 'a'; // cast char to byte, box byte in Byte (min value)
      //aWByte = 'u007F'; // cast char to byte, box byte in Byte (max value)
      //aWByte = 0; // cast int to byte, box byte in Byte (literal)
      //aWByte = +1; // cast int to byte, box byte in Byte (signed literal)
      //aWByte = -1; // cast int to byte, box byte in Byte (signed literal)
      //aWByte = 127; // cast int to byte, box byte in Byte (max value)
      //aWByte = -128; // cast int to byte, box byte in Byte (min value)
      aWByte = aByte; // box byte in Byte (variable)
      aWByte = aWByte; // assign Byte to Byte (variable)
      //aWShort = 'a'; // cast char to short, box short in Short (min value)
      //aWShort = 0; // cast int to short, box short in Short (literal)
      //aWShort = +1; // cast int to short, box short in Short (signed literal)
      //aWShort = -1; // cast int to short, box short in Short (signed literal)
      //aWShort = 32767; // cast int to short, box short in Short (max value)
      //aWShort = -32768; // cast int to short, box short in Short (min value)
      aWShort = aByte; // cast byte to short, box short in Short (variable)
      aWShort = aShort; // box short in Short (variable)
      aWShort = aWByte; // unbox Byte, cast byte to short, box short in Short (variable)
      aWShort = aWShort; // assign Short to Short (variable)
      aWInt = 'a'; // cast char to int, box int in Integer (min value)
      aWInt = 0; // box int in Integer (literal)
      aWInt = +1; // box int in Integer (signed literal)
      aWInt = -1; // box int in Integer (signed literal)
      aWInt = 2147483647; // box int in Integer (max value)
      aWInt = -2147483648; // box int in Integer (min value)
      aWInt = aChar; // cast char to int, box int in Integer (variable)
      aWInt = aByte; // cast byte to int, box int in Integer (variable)
      aWInt = aShort; // cast short to int, box int in Integer (variable)
      aWInt = anInt; // box int in Integer (variable)
      aWInt = aWChar; // unbox Character, cast char to int, box int in Integer (variable)
      aWInt = aWByte; // unbox Byte, cast byte to int, box int in Integer (variable)
      aWInt = aWShort; // unbox Short, cast short to int, box int in Integer (variable)
      aWInt = aWInt; // expected Integer, provided Integer (variable)
      aWLong = 'a'; // cast char to long, box long in Long (min value)
      aWLong = 0; // cast int to long, box long in Long (literal)
      aWLong = 0l; // box long in Long (literal)
      aWLong = +1l; // box long in Long (signed literal)
      aWLong = -1l; // box long in Long (signed literal)
      aWLong = 9223372036854775807l; // box long in Long (max value)
      aWLong = -9223372036854775808l; // box long in Long (min value)
      aWLong = aChar; // cast char to long, box long in Long (variable)
      aWLong = aByte; // cast byte to long, box long in Long (variable)
      aWLong = aShort; // cast short to long, box long in Long (variable)
      aWLong = anInt; // cast int to long, box long in Long (variable)
      aWLong = aLong; // box long in Long (variable)
      aWLong = aWChar; // unbox Character, cast char to long, box long in Long (variable)
      aWLong = aWByte; // unbox Byte, cast byte to long, box long in Long (variable)
      aWLong = aWShort; // unbox Short, cast short to long, box long in Long (variable)
      aWLong = aWInt; // unbox Integer, cast int to long, box long in Long (variable)
      aWLong = aWLong; // expected Long, provided Long (variable)
      aWFloat = 'a'; // cast char to float, box float in Float (min value)
      aWFloat = 0; // cast int to float, box float in Float (literal)
      aWFloat = 0l; // cast long to float, box float in Float (literal)
      //aWFloat = 0f; // box float in Float (literal)
      //aWFloat = 0.f; // box float in Float (literal)
      aWFloat = 0.0f; // box float in Float (literal)
      aWFloat = +0.1f; // box float in Float (signed literal)
      aWFloat = -0.1f; // box float in Float (signed literal)
      aWFloat = aChar; // cast char to float, box float in Float (variable)
      aWFloat = aByte; // cast byte to float, box float in Float (variable)
      aWFloat = aShort; // cast short to float, box float in Float (variable)
      aWFloat = anInt; // cast int to float, box float in Float (variable)
      aWFloat = aLong; // cast long to float, box float in Float (variable)
      aWFloat = aFloat; // box float in Float (variable)
      aWFloat = aWChar; // unbox Character, cast char to float, box float in Float (variable)
      aWFloat = aWByte; // unbox Byte, cast byte to float, box float in Float (variable)
      aWFloat = aWShort; // unbox Short, cast short to float, box float in Float (variable)
      aWFloat = aWInt; // unbox Integer, cast int to float, box float in Float (variable)
      aWFloat = aWLong; // unbox Long, cast long to float, box float in Float (variable)
      aWFloat = aWFloat; // assign Float to Float (variable)
      aWDouble = 'a'; // cast char to double, box double in Double (min value)
      aWDouble = 0; // cast int to double, box double in Double (literal)
      aWDouble = 0l; // cast long to double, box double in Double (literal)
      aWDouble = 0.0f; // cast float to double, box double in Double (literal)
      aWDouble = 0.0; // box double in Double (literal)
      aWDouble = +0.1; // box double in Double (signed literal)
      aWDouble = -0.1; // box double in Double (signed literal)
      aWDouble = aChar; // cast char to double, box double in Double (variable)
      aWDouble = aByte; // cast byte to double, box double in Double (variable)
      aWDouble = aShort; // cast short to double, box double in Double (variable)
      aWDouble = anInt; // cast int to double, box double in Double (variable)
      aWDouble = aLong; // cast long to double, box double in Double (variable)
      aWDouble = aFloat; // cast float to double, box double in Double (variable)
      aWDouble = aDouble; // box double in Double (variable)
      aWDouble = aWChar; // unbox Character, cast char to double, box double in Double (variable)
      aWDouble = aWByte; // unbox Byte, cast byte to double, box double in Double (variable)
      aWDouble = aWShort; // unbox Short, cast short to double, box double in Double (variable)
      aWDouble = aWInt; // unbox Integer, cast int to double, box double in Double (variable)
      aWDouble = aWLong; // unbox Long, cast long to double, box double in Double (variable)
      aWDouble = aWFloat; // unbox Float, cast float to double, box double in Double (variable)
      aWDouble = aWDouble; // assign Double to Double (variable)
      anObject = true; // box boolean in Boolean, assign to Object (true literal)
      anObject = false; // box boolean in Boolean, assign to Object (false literal)
      anObject = 'a'; // box char in Character, assign to Object (literal)
      anObject = 0; // box int in Integer, assign to Object (literal)
      anObject = +1; // box int in Integer, assign to Object (signed literal)
      anObject = -1; // box int in Integer, assign to Object (signed literal)
      anObject = 0l; // box long in Long, assign to Object (literal)
      anObject = +1l; // box long in Long, assign to Object (signed literal)
      anObject = -1l; // box long in Long, assign to Object (signed literal)
      anObject = 0.0f; // box float in Float, assign to Object (literal)
      anObject = +0.1f; // box float in Float, assign to Object (signed literal)
      anObject = -0.1f; // box float in Float, assign to Object (signed literal)
      anObject = 0.0; // box double in Double, assign to Object (literal)
      anObject = +0.1; // box double in Double, assign to Object (signed literal)
      anObject = -0.1; // box double in Double, assign to Object (signed literal)a
      anObject = aWBoolean; // assign Character to Object (variable)
      anObject = aWChar; // assign Character to Object (variable)
      anObject = aWByte; // assign Byte to Object (variable)
      anObject = aWShort; // assign Short to Object (variable)
      anObject = aWInt; // assign Integer to Object (variable)
      anObject = aWLong; // assign Long to Object (variable)
      anObject = aWFloat; // assign Float to Object (variable)
      anObject = aWDouble; // assign Double to Object (variable)
      aNumber = 0; // box int in Integer, assign to Number (literal)
      aNumber = +1; // box int in Integer, assign to Number (signed literal)
      aNumber = -1; // box int in Integer, assign to Number (signed literal)
      aNumber = 0l; // box long in Long, assign to Number (literal)
      aNumber = +1l; // box long in Long, assign to Number (signed literal)
      aNumber = -1l; // box long in Long, assign to Number (signed literal)
      aNumber = 0.0f; // box float in Float, assign to Number (literal)
      aNumber = +0.1f; // box float in Float, assign to Number (signed literal)
      aNumber = -0.1f; // box float in Float, assign to Number (signed literal)
      aNumber = 0.0; // box double in Double, assign to Number (literal)
      aNumber = +0.1; // box double in Double, assign to Number (signed literal)
      aNumber = -0.1; // box double in Double, assign to Number (signed literal)
      aNumber = aWByte; // assign Byte to Number (variable)
      aNumber = aWShort; // assign Short to Number (variable)
      aNumber = aWInt; // assign Integer to Number (variable)
      aNumber = aWLong; // assign Long to Number (variable)
      aNumber = aWFloat; // assign Float to Number (variable)
      aNumber = aWDouble; // assign Double to Number (variable)
      aCpaBoolean = true; // box boolean in Boolean, assign to Comparable<Boolean> (true literal)
      aCpaBoolean = false; // box boolean in Boolean, assign to Comparable<Boolean> (false literal)
      aCpaBoolean = aWBoolean; // assign Character to Comparable<Character> (variable)
      aCpaChar = 'a'; // box char in Character, assign to Comparable<Character> (literal)
      aCpaChar = aWChar; // assign Character to Comparable<Character> (variable)
      aCpaInt = 0; // box int in Integer, assign to Comparable<Integer> (literal)
      aCpaInt = +1; // box int in Integer, assign to Comparable<Integer> (signed literal)
      aCpaInt = -1; // box int in Integer, assign to Comparable<Integer> (signed literal)
      aCpaInt = aWInt; // assign Integer to Comparable<Integer> (variable)
      aCpaLong = 0l; // box long in Long, assign to Comparable<Long> (literal)
      aCpaLong = +1l; // box long in Long, assign to Comparable<Long> (signed literal)
      aCpaLong = -1l; // box long in Long, assign to Comparable<Long> (signed literal)
      aCpaLong = aWLong; // assign Long to Comparable<Long> (variable)
      aCpaFloat = 0.0f; // box float in Float, assign to Comparable<Float> (literal)
      aCpaFloat = +0.1f; // box float in Float, assign to Comparable<Float> (signed literal)
      aCpaFloat = -0.1f; // box float in Float, assign to Comparable<Float> (signed literal)
      aCpaFloat = aWFloat; // assign Float to Comparable<Float> (variable)
      aCpaDouble = 0.0; // box double in Double, assign to Comparable<Double> (literal)
      aCpaDouble = +0.1; // box double in Double, assign to Comparable<Double> (signed literal)
      aCpaDouble = -0.1; // box double in Double, assign to Comparable<Double> (signed literal)
      aCpaDouble = aWDouble; // assign Double to Comparable<Double> (variable)
    }
  }
}
