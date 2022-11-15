/* (c) https://github.com/MontiCore/monticore */
package pumping;

import org.spesml.Stream;
import org.spesml.Time;

spec ElectricToRotationalTrafo(R d)

  in  HStream<ElectricalEnergy> ei;
  out HStream<RotationalEnergy> rot;

  forall t in R:
    rot.nth(t+d).p == ei.nth(t).p &&
    rot.nth(t) == rot.nth(t).tau * rot.nth(t).w &&
    0 <= t <= d implies rot.nth(t).p == 0;
end