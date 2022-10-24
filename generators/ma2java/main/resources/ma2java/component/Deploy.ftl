<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the deployment class for a component. -->
${tc.signature("comp", "isTop")}

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<#assign deployClassName> Deploy${comp.getName()}<#if isTop>TOP</#if> </#assign>

public class ${deployClassName} {

  public static void main(String[] args) {
    montiarc.rte.DeployUtils deployUtils = new montiarc.rte.DeployUtils();

    if(!deployUtils.parseArgs(args)) {
      return;
    }

    montiarc.rte.log.Log.initFileLog(deployUtils.getLogPath());
    montiarc.rte.log.Log.setTraceEnabled(true);

    final ${comp.getName()} comp = new ${comp.getName()}();

    comp.setUp();
    comp.init();

    long time;

    for(int cycles = 0; cycles < deployUtils.getMaxCyclesCount(); cycles++) {
      montiarc.rte.log.Log.trace("::: Time t = " + cycles + " :::");
      time = System.currentTimeMillis();
      comp.compute();
      comp.update();
      while((System.currentTimeMillis() - time) < deployUtils.getCycleTime()) {
        Thread.yield();
      }
    }
  }
}