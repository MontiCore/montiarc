<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu")}
<#include "/templates/Utils.ftl">
${p}

public class Deploy${fmu.getName()}
        extends montiarc.rte.deploy.Deployment<${fmu.getName()}Comp> {

  public Deploy${fmu.getName()}(montiarc.rte.deploy.DeploymentStrategy<${fmu.getName()}Comp> strategy) {
    super(strategy);
  }

  @Override
  protected ${fmu.getName()}Comp buildComponent(montiarc.rte.scheduling.CoordinatingScheduler scheduler,
                                          java.util.Map<String, String> parameters) {
    ${fmu.getName()}CompBuilder builder = new ${fmu.getName()}CompBuilder("${fmu.getName()}")
            .setScheduler(scheduler);
    return builder.build();
  }
}
