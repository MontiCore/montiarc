/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SJava {
  port in String i;
  port out String o;

  <<sync>> compute {
    if (i.isBlank()) {
      o = "isBlank";
    } else {
      o = i;
    }
  }
}
