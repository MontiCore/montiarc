/* (c) https://github.com/MontiCore/monticore */

component UnderscoreNames {
  constraint (a_b.c && a.b_c);

  component AB a_b {
    feature c;
  }

  component A a {
    feature b_c;
  }
}
