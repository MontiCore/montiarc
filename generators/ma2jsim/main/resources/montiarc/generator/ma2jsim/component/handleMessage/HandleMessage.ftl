<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

@Override
public void handleMessage(montiarc.rte.port.IInPort<?> receivingPort) {
if (this.isAtomic) {
    ${tc.include("montiarc.generator.ma2jsim.component.handleMessage.AtomicMethodBody.ftl")}
} else {
    ${tc.include("montiarc.generator.ma2jsim.component.handleMessage.DecomposedMethodBody.ftl")}
}
}

${tc.include("montiarc.generator.ma2jsim.component.handleMessage.HandlePortForward.ftl")}
