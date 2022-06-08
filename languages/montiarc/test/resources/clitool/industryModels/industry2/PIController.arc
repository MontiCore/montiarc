/* (c) https://github.com/MontiCore/monticore */
package industryModels.industry2;

/**
 * Valid model.
 * This is a deliberate duplicate of ../industry/PIController.arc, don't delete it.
 */
component PIController {
  port in Integer rotorSpeed,
        out Integer desiredAngle;
}
