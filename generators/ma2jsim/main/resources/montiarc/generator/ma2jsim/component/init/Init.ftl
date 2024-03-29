<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
@Override
public void init() {
  if (this.isAtomic) {
      if (<@MethodNames.getBehavior/>() != null)
        this.schedule(<@MethodNames.getBehavior/>()::init);
  } else {
      this.getAllSubcomponents().forEach(montiarc.rte.component.ITimedComponent::init);
  }
  <#if helper.getModeAutomaton(ast).isPresent()>
      modeAutomaton.init();
  </#if>
  if (this.getAllInPorts().isEmpty()) this.scheduleTick();
}
