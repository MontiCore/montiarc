/* (c) https://github.com/MontiCore/monticore */
package scmapping;

public class SCMappingMill extends SCMappingMillTOP {

  public static void reset() {

    SCMappingMillTOP.reset();
    millSCMappingInheritanceHandler = null;
  }
}
