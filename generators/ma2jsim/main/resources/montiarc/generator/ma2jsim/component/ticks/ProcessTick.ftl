<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()/>

@Override
public void <@MethodNames.handleTick/>() {
  if (this.isAtomic) {
      if (isSync && <@MethodNames.getBehavior/>() != null && <@MethodNames.inputsTickBuffered/>()) {
        <@MethodNames.getBehavior/>().tick();
        getAllInPorts().forEach(inP -> {
          while (inP.hasBufferedTick() && !inP.isTickBlocked())
            de.se_rwth.commons.logging.Log.warn(
              "Component " + this.getName() +
                " has received more than one data message in a single time slice on port " + inP.getQualifiedName() +
                ". Dropped data: " + inP.pollBuffer());
        });
      }

      if (<@MethodNames.getBehavior/>() == null) {
        <#-- No behavior behavior -->
        getAllInPorts().forEach(inP -> {
          while (inP.hasBufferedTick() && !inP.isTickBlocked()) inP.pollBuffer();
        });
      }


      if(!<@MethodNames.inputsTickBlocked/>()) throw new java.lang.RuntimeException("Component " + getName() + " is trying to tick even though some inputs are not tick blocked.");

      if(!this.isSync && <@MethodNames.getBehavior/>() != null) <@MethodNames.getBehavior/>().tick();
      <@MethodNames.dropTickOnAll/>();
      <@MethodNames.sendTickOnAll/>();
      if(!isSync || <@MethodNames.inputsTickBuffered/>()) {
        getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);
      }
  } else {
      if(!<@MethodNames.inputsTickBlocked/>()) throw new java.lang.RuntimeException("Component " + getName() + " is trying to tick even though some inputs are not tick blocked.");
      <#if hasModeAutomaton>
          <@MethodNames.getModeAutomaton/>().tick();
      </#if>
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(montiarc.rte.port.TimeAwarePortForward::forward);
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(fwd -> {
          while(!fwd.isBufferEmpty() && !fwd.isTickBlocked()) fwd.forward();
        });
  }
  if (this.getAllInPorts().isEmpty()) this.scheduleTick();
}
