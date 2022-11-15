/* (c) https://github.com/MontiCore/monticore */
package pumping;

import org.spesml.Stream;
import org.spesml.Time;

spec ElectricPowerController(R d)
  in  HStream<Power> p_tgt;
  in  HStream<ElectricalEnergy> ei;
  out HStream<ElectricalEnergy> ctrl;

  forall t in R:
    ctrl.nth(t+d).p == ei.nth(t).p && ctrl.nth(t+d) == p_tgt(t) &&
    ctrl.nth(t+d).p == (p_tgt.nth(t) - ctrl.nth(t)) * ctrl.nth(t) / d &&
    0 <= t <= d implies ctrl.nth(t).p == 0;
end
