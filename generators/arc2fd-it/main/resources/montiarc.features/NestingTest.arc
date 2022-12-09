/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component NestingTest {
  feature a, b, c, d;
  //constraint ((a && b && c) > 2);
  constraint (3 > 2);
  constraint (a > 2);
  constraint (a < 2);
  constraint (3 < d);
  constraint (a > b);
  constraint ((a / 3) != 5);
  //constraint ((4 > 2) ? a : b);
  //constraint ((a > 3) && b || c);

  //constraint (!(a && b) && (a || b));
  /*
  component innerOne {
    feature f;
  }

  SubComp comp1(sa=0, sb=1);

  if (3 < 2) {
    feature b, e, f, g;
    constraint (a == b);
  } else {
    feature h;
    SubComp comp2;
    SubComp comp3;
    constraint ((a != b));
  }

  if( 5 > 3) {
    feature JAA;
  }

  constraint ((5 > 4) && (d == c) && (a ==b));
  */

  //constraint (b && (a > 3));
}
