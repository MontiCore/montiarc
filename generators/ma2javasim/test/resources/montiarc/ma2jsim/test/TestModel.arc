/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component TestModel(double param1, char param2, int defaultParam = 1) {

  port <<sync>> in Integer i;
  port <<sync, delayed>> out boolean o;

  feature myFeature;

  constraint (myFeature);

  int myField = 0;
  String fieldWithInitial = "abcd";

  Other other(1,2,4);
  Other other2(11,22);
  Other other3(first = 111, second = 222);
  Other other4(first = 111, second = 222, withDefault = 333);

  i -> other.i;
  other.o -> other2.i;
  other2.o -> other3.i;
  other3.o -> other4.i;
  other4.b -> o;
}
