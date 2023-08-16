/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component TestDeploy() {

  feature myFeature;

  constraint (!myFeature);

  int myField = 0;
  String fieldWithInitial = "abcd";

  Source source;
  Other other(1,2,4);
  Other other2(11,22);
  Other other3(first = 111, second = 222);
  Other other4(first = 111, second = 222, withDefault = 333);

  source.o -> other.i;
  other.o -> other2.i;
  other2.o -> other3.i;
  other3.o -> other4.i;
}
