<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu", "fmuFile")}
<#include "/templates/Utils.ftl">
${p}
import de.se_rwth.commons.logging.Log;
import no.ntnu.ihb.fmi4j.importer.fmi2.*;
import no.ntnu.ihb.fmi4j.*;
import no.ntnu.ihb.fmi4j.importer.fmi2.Fmu;
import no.ntnu.ihb.fmi4j.modeldescription.variables.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import montiarc.rte.Simulation;
import montiarc.lang.Duration;

public class ${fmu.getName()}Compute
    extends montiarc.rte.behavior.AbstractBehavior<${ContextClass}, ${syncMsgClass}>
    implements ${eventClass} {


  File fmuFile;
  Fmu fmu;
  CoSimulationSlave fmuInstance;
  {
    try {
      String fmuResourcePath = "/${packageName}/${fmuFile.getName()}";
      java.io.InputStream fmuInputStream = getClass().getResourceAsStream(fmuResourcePath);
      if (fmuInputStream == null) {
        throw new IOException("FMU resource '" + fmuResourcePath + "' not found on classpath");
      }

      fmuFile = java.io.File.createTempFile("${fmuFile.getName()?keep_before_last('.')}", ".fmu");
      fmuFile.deleteOnExit();
      java.nio.file.Files.copy(fmuInputStream, fmuFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      fmuInputStream.close();

      fmu = Fmu.from(fmuFile);
      fmuInstance = fmu.asCoSimulationFmu().newInstance();
    } catch (IOException e) {
      de.se_rwth.commons.logging.Log.error("Failed to load FMU", e);
    }
  }

  double t = 0.0;

  protected ${fmu.getName()}Compute(
      ${ContextClass} fmuContext, String name) {
    super(fmuContext, name);
  }
  @Override
  public void init() {
    <#list fixedParamVars as v>
    ${toJavaType(v.getType())} ${sanitizeName(v.getName())} = context.param_${sanitizeName(v.getName())}();
    </#list>
    this.fmuInstance.setupExperiment(0.0, 0.0, 0.0);
    this.fmuInstance.enterInitializationMode();
    <#list fixedParamVars as v>
      fmuInstance.write${toFmi4jType(v.getType())}(new long[]{${v.getValueReference()?c}}, new ${toJavaType(v.getType())}[]{${sanitizeName(v.getName())}});
    </#list>
    this.fmuInstance.exitInitializationMode();
    {
    }
  }

  @Override
  public void tick(${syncMsgClass} msg) {
    realTick(<#list inputVars as v>msg.${sanitizeName(v.getName())}<#if v_has_next>, </#if></#list>);
  }

  protected void realTick(
<#list inputVars as v> ${toJavaType(v.getType())} ${sanitizeName(v.getName())}<#if v_has_next>, </#if></#list>) {
    double dt = Simulation.nanosecondsPerTick / 1_000_000_000.0;
<#list inputVars as v>
  fmuInstance.write${toFmi4jType(v.getType())}(new long[]{${v.getValueReference()?c}}, new ${toJavaType(v.getType())}[]{${sanitizeName(v.getName())}});
</#list>
<#list outputVars as v>
  ${toJavaType(v.getType())}[] ${sanitizeName(v.getName())}arr = new ${toJavaType(v.getType())}[1];
</#list>
    fmuInstance.doStep(t, dt);
    {
<#list outputVars as v>
    fmuInstance.read${toFmi4jType(v.getType())}(new long[]{${v.getValueReference()?c}}, ${sanitizeName(v.getName())}arr);
    context.port_${sanitizeName(v.getName())}().send(${sanitizeName(v.getName())}arr[0]);
</#list>
    }
    t += dt;
  }

  <#list syncInputVars as v>
  @Override
  public void msg_${sanitizeName(v.getName())}(${toJavaType(v.getType())} msg) {
    de.se_rwth.commons.logging.Log.warn("The message cannot be handled by compute behavior and will be ignored");
    }
  </#list>

  <#list tunParamVars as v>
  @Override
  public void msg_${sanitizeName(v.getName())}(${toJavaType(v.getType())} msg) {
    fmuInstance.write${toFmi4jType(v.getType())}(new long[]{${v.getValueReference()?c}}, new ${toJavaType(v.getType())}[]{msg});
  }
  </#list>
  @Override
  public boolean isDelayed() {
    return false;
  }
}
