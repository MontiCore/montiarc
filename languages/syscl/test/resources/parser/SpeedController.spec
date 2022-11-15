/* (c) https://github.com/MontiCore/monticore */
spec SpeedController

  in Motion is;
  in Speed tgt;
  out Motion ctrld;

  forall t in R:
    pi1(ctrld[t + g]) == pi1(is[t]) * e(norm2(pi1(is)), tgt, t) / g;

end