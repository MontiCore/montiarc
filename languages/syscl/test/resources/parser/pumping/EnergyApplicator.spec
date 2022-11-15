/* (c) https://github.com/MontiCore/monticore */
package pumping;

import org.spesml.Stream;
import org.spesml.Time;

spec EnergyApplicator(R d)
  in Stream<RotationalEnergy> r;
  in Stream<Fluid> fi;
  out Stream<Fluid > fo;

  forall t in R:
    fo.nth(t+d).c.p * fo.nth(t+d).c.q == max(r.nth(t).p, fi.nth(t).c.p * fi.nth(t).c.q ) &&
    fo.nth(t).c.pwr == fo.nth(t).c.q * fo.nth(t).c.p &&
    0 <= t <= d implies fo.nth(t).c.p == 0;

end
