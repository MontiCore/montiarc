/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SComp(String s) {
  port sync in String i;
  port sync out String o;

  component SInner(String p) {
    port sync in String i;
    port sync out String o;
  }

  SInner sub1(s);
  SPara sub2(s);

  i -> sub1.i;
  sub1.o -> o;
}
