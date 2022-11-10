/* (c) https://github.com/MontiCore/monticore */
package elevator;

component Splitter {

  port <<sync>> in Integer i,
       <<sync>> out boolean o1, o2, o3, o4;

  compute {
    if (i == null) {
      o1 = false;
      o2 = false;
      o3 = false;
      o4 = false;
    } else {
      switch(i) {
        case 1: {
          o1 = true;
          o2 = false;
          o3 = false;
          o4 = false;
          break;
        }
        case 2: {
          o1 = false;
          o2 = true;
          o3 = false;
          o4 = false;
          break;
        }
        case 3: {
          o1 = false;
          o2 = false;
          o3 = true;
          o4 = false;
          break;
        }
        case 4: {
          o1 = false;
          o2 = false;
          o3 = false;
          o4 = true;
          break;
        }
        default: {
          o1 = false;
          o2 = false;
          o3 = false;
          o4 = false;
          break;
        }
      }
    }
  }

}
