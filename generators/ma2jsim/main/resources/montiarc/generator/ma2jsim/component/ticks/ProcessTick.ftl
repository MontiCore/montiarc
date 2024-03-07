<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()/>

public void <@MethodNames.handleTick/>() {
  if (this.isAtomic) {
      if (isSync && <@MethodNames.getBehavior/>() != null && getAllInPorts().stream().allMatch(montiarc.rte.port.IInPort::hasBufferedTick)) {
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


      if(!<@MethodNames.inputsTickBlocked/>()) return;

      if(!this.isSync && <@MethodNames.getBehavior/>() != null) <@MethodNames.getBehavior/>().tick();
      <@MethodNames.dropTickOnAll/>();
      <@MethodNames.sendTickOnAll/>();
      if(!isSync || getAllInPorts().stream().allMatch(montiarc.rte.port.ITimeAwareInPort::hasBufferedTick)) {
        getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);
      }
  } else {
      if(!<@MethodNames.inputsTickBlocked/>()) return;
      <#if hasModeAutomaton>
          <@MethodNames.getModeAutomaton/>().tick();
      </#if>
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(montiarc.rte.port.TimeAwarePortForward::forward);
      <#list helper.getSubcomponentsWithoutInPorts(ast) as sub>
          ${prefixes.subcomp()}${sub.getName()}${helper.subcomponentVariantSuffix(ast, sub.getSymbol())}().<@MethodNames.handleTick/>();
      </#list>
      getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.TimeAwarePortForward)
        .map(p -> (montiarc.rte.port.TimeAwarePortForward<?>) p)
        .forEach(fwd -> {
          while(!fwd.isBufferEmpty() && !fwd.isTickBlocked()) fwd.forward();
        });
  }
}
