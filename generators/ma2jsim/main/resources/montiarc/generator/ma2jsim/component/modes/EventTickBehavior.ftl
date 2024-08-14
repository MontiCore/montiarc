<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

@Override
public void tick(${ast.getName()}${suffixes.syncMsg()} nullMsg) {
${tc.include("montiarc.generator.ma2jsim.component.modes.TickBehaviorBody.ftl")}
}
