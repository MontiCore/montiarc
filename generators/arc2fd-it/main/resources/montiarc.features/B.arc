/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component B {
  feature u, v, w, x, y, z;

  constraint (!(x && y) && (x || y));
  constraint (!(v && w) && (v || w));
}
