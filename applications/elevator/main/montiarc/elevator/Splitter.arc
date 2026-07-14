/* (c) https://github.com/MontiCore/monticore */
package elevator;

component Splitter {

  port sync in Integer i,
       sync out Boolean o1, o2, o3, o4;

  compute {
    if (i == null) {
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = false;
    } else if (i == 1) {
      o1 = true;
      o2 = false;
      o3 = false;
      o4 = false;
    } else if (i == 2) {
      o1 = false;
      o2 = true;
      o3 = false;
      o4 = false;
    } else if (i == 3) {
      o1 = false;
      o2 = false;
      o3 = true;
      o4 = false;
    } else if (i == 4) {
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = true;
    } else {
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = false;
    }
  }

}
