/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.string;

component ToString() {
  port in Object i;
  port out String o;

  <<sync>> compute {
    o = i.toString();
  }
}
