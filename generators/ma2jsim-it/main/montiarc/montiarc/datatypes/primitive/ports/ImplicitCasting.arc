/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

// In this test, we test whether primitive implicit type casting of port values works.
// To do this, we will try to assign input port values to local fields in the behavior.
// We will then assign input port values directly to output ports, as well as local field values to output ports.
// With the assignments we want to test all possible implicit primitive type castings, with the assignments
// behaviorField = inPort; outPort = inPort, outPort = behaviorField;
component ImplicitCasting {
  port in byte inByte,
       in short inShort,
       in int inInt,
       in long inLong,
       in float inFloat,
       in double inDouble,
       in char inChar,
       in boolean inBool;

  port out byte outByte,
       out short outShort,
       out int outInt,
       out long outLong,
       out float outFloat,
       out double outDouble,
       out char outChar,
       out boolean outBoolean;

  <<timed>> automaton {
    initial state S;

    // All transitions triggered by inByte will try to assign bytes to outPorts / fields etc.
    // All transitions triggered by inShort will try to assign shorts to outPorts / fields etc.
    // It's the same for the other primitives
    S -> S inByte / {
      outByte = inByte;
      outShort = inByte;
      outInt = inByte;
      outLong = inByte;
      outFloat = inByte;
      outDouble = inByte;
      outChar = 'a';       // Not assignable from byte
      outBoolean = false;  // Not assignable from byte
    };

    S -> S inByte / {
      byte interByte = inByte;
      short testAssignToShortField = inByte;
      int testAssignToIntField = inByte;
      long testAssignToLongField = inByte;
      float testAssignToFloatField = inByte;
      double testAssignToDoubleField = inByte;

      outByte = interByte;
      outShort = interByte;
      outInt = interByte;
      outLong = interByte;
      outFloat = interByte;
      outDouble = interByte;
      outChar = 'a';       // Not assignable from byte
      outBoolean = false;  // Not assignable from byte
    };

    S -> S inShort / {
      // Leaving out outByte as it is not assignable from short,
      // its corresponding input-port value is not present (as inShort was the triggerer)
      // and MontiCore does not provide constants of type byte
      outShort = inShort;
      outInt = inShort;
      outLong = inShort;
      outFloat = inShort;
      outDouble = inShort;
      outChar = 'a';       // Not assignable from short
      outBoolean = false;  // Not assignable from short
    };

    S -> S inShort / {
      short interShort = inShort;
      int testAssignToIntField = inShort;
      long testAssignToLongField = inShort;
      float testAssignToFloatField = inShort;
      double testAssignToDoubleField = inShort;

      // See the other inShort-transition to see why outByte does not appear here
      outShort = interShort;
      outInt = interShort;
      outLong = interShort;
      outFloat = interShort;
      outDouble = interShort;
      outChar = 'a';       // Not assignable from short
      outBoolean = false;  // Not assignable from short
    };

    S -> S inInt / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = inInt;
      outLong = inInt;
      outFloat = inInt;
      outDouble = inInt;
      outChar = 'a';       // Not assignable from int
      outBoolean = false;  // Not assignable from int
    };

    S -> S inInt / {
      int interInt = inInt;
      long testAssignToLongField = inInt;
      float testAssignToFloatField = inInt;
      double testAssignToDoubleField = inInt;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = interInt;
      outLong = interInt;
      outFloat = interInt;
      outDouble = interInt;
      outChar = 'a';       // Not assignable from int
      outBoolean = false;  // Not assignable from int
    };

    S -> S inLong / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from long
      outLong = inLong;
      outFloat = inLong;
      outDouble = inLong;
      outChar = 'a';       // Not assignable from long
      outBoolean = false;  // Not assignable from long
    };

    S -> S inLong / {
      long interLong = inLong;
      float testAssignToFloatField = inLong;
      double testAssignToDoubleField = inLong;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from long
      outLong = interLong;
      outFloat = interLong;
      outDouble = interLong;
      outChar = 'a';       // Not assignable from long
      outBoolean = false;  // Not assignable from long
    };

    S -> S inFloat / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from float
      outLong = 0;         // Not assignable from float
      outFloat = inFloat;
      outDouble = inFloat;
      outChar = 'a';       // Not assignable from float
      outBoolean = false;  // Not assignable from float
    };

    S -> S inFloat / {
      float interFloat = inFloat;
      double testAssignToDoubleField = inFloat;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from float
      outLong = 0;         // Not assignable from float
      outFloat = interFloat;
      outDouble = interFloat;
      outChar = 'a';       // Not assignable from float
      outBoolean = false;  // Not assignable from float
    };

    S -> S inDouble / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from double
      outLong = 0;         // Not assignable from double
      outFloat = 0.0f;     // Not assignable from double
      outDouble = inDouble;
      outChar = 'a';       // Not assignable from double
      outBoolean = false;  // Not assignable from double
    };

    S -> S inDouble / {
      double interDouble = inDouble;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;          // Not assignable from double
      outLong = 0;         // Not assignable from double
      outFloat = 0.0f;     // Not assignable from double
      outDouble = interDouble;
      outChar = 'a';       // Not assignable from double
      outBoolean = false;  // Not assignable from double
    };

    S -> S inChar / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = inChar;
      outLong = inChar;
      outFloat = inChar;
      outDouble = inChar;
      outChar = inChar;
      outBoolean = false;  // Not assignable from char
    };

    S -> S inChar / {
      char interChar = inChar;
      int testAssignToIntField = inChar;
      long testAssignToLongField = inChar;
      float testAssignToFloatField = inChar;
      double testAssignToDoubleField = inChar;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = interChar;
      outLong = interChar;
      outFloat = interChar;
      outDouble = interChar;
      outChar = interChar;
      outBoolean = false;  // Not assignable from char
    };

    S -> S inBool / {
      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;        // Not assignable from boolean
      outLong = 0;       // Not assignable from boolean
      outFloat = 0.0f;   // Not assignable from boolean
      outDouble = 0.0;   // Not assignable from boolean
      outChar = 'a';     // Not assignable from boolean
      outBoolean = inBool;
    };

    S -> S inBool / {
      boolean interBool = inBool;

      // See a inShort-triggered transition for a reason to leave out outShort and outByte
      outInt = 0;        // Not assignable from boolean
      outLong = 0;       // Not assignable from boolean
      outFloat = 0.0f;   // Not assignable from boolean
      outDouble = 0.0;   // Not assignable from boolean
      outChar = 'a';     // Not assignable from boolean
      outBoolean = interBool;
    };
  }
}
