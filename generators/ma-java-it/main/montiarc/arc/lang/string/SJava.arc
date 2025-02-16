/* (c) https://github.com/MontiCore/monticore */
package arc.lang.string;

component SJava {
  port sync in String i;
  port sync out String o;

  compute {
    if (i.isBlank()) {
      o = "isBlank";
    } else {
      o = i;
    }
  }
}
