/* (c) https://github.com/MontiCore/monticore */
package simple;

import montiarc.lang;

component DecomposedComp {
  // feature f;

  int i = 0;

  //AType id = null;

  AtomicComp aC();
  Delay<int> dComp;
  aC.p2 -> dComp.i;
  dComp.o -> aC.p;

  // varif (f) {

  // }

  // constraint(!f);
}
