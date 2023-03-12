/* (c) https://github.com/MontiCore/monticore */
package prepost;

spec Nor {

  port in boolean x, y;
  port out boolean z;

  ------------------------

  post: !x && !y  <=>  z

}
