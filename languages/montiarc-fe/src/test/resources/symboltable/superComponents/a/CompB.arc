/* (c) https://github.com/MontiCore/monticore */
package a;

import components.body.subcomponents._subcomponents.HasIntegerInputAndOutput;

/*
 * Valid model.
 */
component CompB extends HasIntegerInputAndOutput {
  port
    in String str,
    out Integer int3;
}
