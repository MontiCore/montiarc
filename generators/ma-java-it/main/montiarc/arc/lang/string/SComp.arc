/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SComp(String s) {
  port in String i;
  port out String o;

  component SInner(String p) {
    port in String i;
    port out String o;
  }

  SInner sub1(s);
  SPara sub2(s);

  i -> sub1.i;
  sub1.o -> o;
}
