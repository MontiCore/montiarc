<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)/>
<#assign hasModeAutomaton = modeAutomatonOpt.isPresent()/>

@Override
public void handleTick() {
  if (isSync) {
    handleSyncedTickExecution();
  } else {
    handleEventTickExecution();
  }
}
