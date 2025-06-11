/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.string;

component ToString() {
  port sync in Object i;
  port sync out String o;

  compute {
    o = i.toString();
  }
}
