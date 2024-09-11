/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component SubComp {
  feature sa, sb, sc;

  //constraint (!sa || !sb || sc);


  varif (3 < 2) {
    feature ASDF;
  } else {
    feature EFGG;

    constraint (!(EFGG && sa));
  }
}
