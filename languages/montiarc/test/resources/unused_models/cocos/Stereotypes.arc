/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Valid model.
 */
<<author="Haber", test>>
component Stereotypes {

  <<test3>>
  port
    <<test5>> out String out1,
    <<test5>> out String out2;

  <<test2>>
  component InnerC<T>(int x, int y) {
    <<stst>>
    port
      <<asdasd>> in T inin,
      out T outout;
  }

  <<a, s, d, f>>
  component InnerC<String>(2,3)
    ic1 [<<fooo>> outout -> out1, out2;
          <<bar>> outout -> ic2.inin],
    ic2 [<<AW="Wurst">> outout -> ic1.inin];
}
