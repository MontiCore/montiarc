/* (c) https://github.com/MontiCore/monticore */
package pumping;

spec HydraulicPump(R d)
  in HStram<Power> power_tgt;
  in HStream<ElectricalEnergy> p_supply;
  in HStream<Fluid> pi;
  out HStream<Fluid> po;

  ElectricPowerController(power_tgt, p_supply, crtl, d);
  ElectricToRotationalTrafo(crtl, rot, d);
  EnergyApplicator(rot, pi, po, d);

end
