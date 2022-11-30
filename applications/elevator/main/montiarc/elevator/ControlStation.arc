/* (c) https://github.com/MontiCore/monticore */
package elevator;

component ControlStation {

  port <<sync>> in boolean btn1, btn2, btn3, btn4,
       <<sync>> out boolean light1, light2, light3, light4,
       <<sync>> out boolean req1, req2, req3, req4;
  port <<sync>> in int clear;

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

  Splitter splitter;

  clear -> splitter.i;
  splitter.o1 -> floor1.clear;
  splitter.o2 -> floor2.clear;
  splitter.o3 -> floor3.clear;
  splitter.o4 -> floor4.clear;

}
