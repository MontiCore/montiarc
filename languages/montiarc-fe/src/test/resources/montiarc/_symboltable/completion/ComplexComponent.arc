/* (c) https://github.com/MontiCore/monticore */
package completion;

/**
 * Valid model.
 */
component ComplexComponent (int par1, String par2, Integer par3) {
  port in  int pi_1;
  port out int po_1;
  port in  int pi_2, pi_3;
  port out int po_2,
       out boolean po_3;

  int var1 = 5;
  String var2 = "five";

  component Inner(int x, int y) {
    port in  int pi_1, pi_2, pi_3;
    port out int po_i,
         out boolean po_b;

    int var = x + y;
  }

  Inner inner1(1, 2), inner2(3, 4);

  pi_1 -> inner1.pi_1;
  pi_2 -> inner1.pi_2;
  pi_3 -> inner1.pi_3;

  pi_1 -> inner2.pi_1;
  pi_2 -> inner2.pi_2;
  pi_3 -> inner2.pi_3;

  inner1.po_i -> po_1;
  inner2.po_i -> po_2;

  inner1.po_b -> or.a;
  inner2.po_b -> or.b;

  component Or or {
    port in  boolean a, b,
         out boolean c;
    automaton {
      initial { System.out.println("enter s1"); boolean foo = false; c = foo; } state s1;
      state s2;
      s1 -> s1 [ a || b == false ] / { System.out.println("enter s1"); boolean foo = false; c = foo; };
      s1 -> s2 [ a || b == true ] / { System.out.println("enter s2"); boolean foo = false; c = foo; };
      s2 -> s1 [ a || b == false ] / { System.out.println("enter s1"); boolean foo = false; c = foo; };
      s2 -> s2 [ a || b == true ] / { System.out.println("enter s2"); boolean foo = false; c = foo; };
    }
  }

  or.c -> po_3;

  autoconnect off;
  autoinstantiate off;
}
