/* (c) https://github.com/MontiCore/monticore */
package validFileStructureMock.validPackageMock2;

/**
 * Valid model. The content of this model does not matter. It solely has the purpose to provide a valid directory
 * structure and to be a valid model (content irrelevant), so that we can test proper I/O behavior of the MontiArcTool
 * methods that work on paths, files, and directories (such as MontiArcTool#parse(String, Path)).
 */
component ValidMockComponent2 {
  port in Integer rotorSpeed,
        out Integer desiredAngle;
}
