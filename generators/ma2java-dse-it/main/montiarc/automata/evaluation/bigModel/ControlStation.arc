/* (c) https://github.com/MontiCore/monticore */
package evaluation.bigModel;

component ControlStation(Integer parameter) {

  port <<sync>> in Boolean btn1, btn2, btn3, btn4,
       <<sync>> out Boolean light1, light2, light3, light4,
       <<sync>> out Boolean req1, req2, req3, req4;
  port <<sync>> in Integer clear;

  FloorControl floor1, floor2, floor3, floor4;

  btn1 -> floor1.btn;
  btn2 -> floor2.btn;
  btn3 -> floor3.btn;
  btn4 -> floor4.btn;

  floor1.light -> light1;
  floor2.light -> light2;
  floor3.light -> light3;
  floor4.light -> light4;

  floor1.req -> req1;
  floor2.req -> req2;
  floor3.req -> req3;
  floor4.req -> req4;

  Splitter splitter(parameter);

  clear -> splitter.ins;
  splitter.o1 -> floor1.clear;
  splitter.o2 -> floor2.clear;
  splitter.o3 -> floor3.clear;
  splitter.o4 -> floor4.clear;

}
