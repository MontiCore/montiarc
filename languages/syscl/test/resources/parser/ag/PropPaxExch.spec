/* (c) https://github.com/MontiCore/monticore */
package ag;

spec PropPaxExch {

  port in TSStream<Boolean> pax;
  port out TSStream<Propulsion> o;

  guarantee: forall Time t: if pax.t == true then {prop.nt(t), prop.nt(t+1)}.contains(PropulsionCut()) else chaos();

}
