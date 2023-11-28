<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign automaton = helper.getAutomatonBehavior(ast)/>
<#assign hasAutomaton = automaton.isPresent()/>
<#assign isEvent = hasAutomaton && helper.isEventBased(automaton.get())/>

protected ${ast.getName()}${suffixes.automaton()}<@Util.printTypeParameters ast false/> automaton;

@Override
public ${ast.getName()}${suffixes.automaton()}<@Util.printTypeParameters ast false/> <@MethodNames.getBehavior/>() { return automaton; }

protected void <@MethodNames.behaviorSetup/>() {
    this.automaton = new ${ast.getName()}${suffixes.automaton()}${suffixes.builder()}<@Util.printTypeParameters ast false/>(this)
    <#if !isEvent>
        .addDefaultTransitions()
    </#if>
      .addDefaultStates()
      .setDefaultInitial()
      .build();
}

<#if !isEvent>
    protected void <@MethodNames.handleSyncComputation/>() {
      if(getAllInPorts().stream().allMatch(montiarc.rte.port.IInPort::hasBufferedTick)) {
        <@MethodNames.getBehavior/>().tick();
        getAllInPorts().forEach(inP -> {
          while(inP.hasBufferedTick() && !inP.isTickBlocked()) de.se_rwth.commons.logging.Log.warn(
            "Component " + this.getName() +
            " has received more than one data message in a single time slice on port " + inP.getQualifiedName() +
            ". Dropped data: " + inP.pollBuffer());
        });
        <@MethodNames.handleTick/>();
      }
    }
</#if>