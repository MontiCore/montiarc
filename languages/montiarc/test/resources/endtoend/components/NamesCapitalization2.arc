/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: Several identifiers have invalid casing.
 */
component NamesCapitalization2<t1, t2>(int P1, int P2) {
  port in int I1,
       out int O1;
  port in int I2;
  port out int O2;

  int V1 = 0;

  component inner<t3>(int P3) {
    port in t3 I3, I4;
    port out t3 O3;

    int V2 = 0;

    automaton {
      initial state s1;
      state s2 {
        state s3 {
          state s4;
        }
        state s5;
      }
    }
  }

  inner<int> Sub1(P3 = 0), Sub2(0);

  I1 -> Sub1.I3;
  I1 -> Sub2.I3;
  I2 -> Sub1.I4;
  I2 -> Sub2.I4;
  Sub1.O3 -> O1;
  Sub2.O3 -> O2;
}
