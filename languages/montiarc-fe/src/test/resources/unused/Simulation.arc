package test;

component Simulation {
  port in double r;
  
  component Controller {
    port in  double r,
         in  double y,
         out double u;
  }
  
  component Controller ctrl;
  
  component Plant {
    port in  double u,
         out double y;
  }
}