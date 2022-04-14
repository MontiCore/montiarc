<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the deployment class for a component. -->
${tc.signature("comp", "helper", "isTOPClass")}
<#assign comp=comp><#assign helper=helper>
<#import "/templates/util/Utils.ftl" as Utils>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

import de.montiarc.runtimes.log.Log;
import de.montiarc.runtimes.DeployUtils;

<#assign compName = comp.getName()>
<#assign deployClassName> Deploy${compName}<#if isTOPClass>TOP</#if> </#assign>

public class ${deployClassName} {

  public static void main(String[] args) {
    DeployUtils deployUtils = new DeployUtils();

    if(!deployUtils.parseArgs(args)) {
      return;
    }

    Log.initFileLog(deployUtils.getLogPath());

    final ${compName} cmp = new ${compName}();

    cmp.setUp();
    cmp.init();

    long time;

    for(int cycles = 0; cycles < deployUtils.getMaxCyclesCount(); cycles++) {
      time = System.currentTimeMillis();
      cmp.compute();
      cmp.update();
      while((System.currentTimeMillis() - time) < deployUtils.getCycleTime()) {
        Thread.yield();
      }
    }
  }
}