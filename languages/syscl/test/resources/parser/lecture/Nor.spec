/* (c) https://github.com/MontiCore/monticore */
spec Nor {

  port in boolean x, y;
  port out boolean z;

  ------------------------

  post: !x && !y  <=>  z

}
