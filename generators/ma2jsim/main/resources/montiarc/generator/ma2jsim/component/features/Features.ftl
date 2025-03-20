<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#list helper.getFeatures(ast) as feature>
    ${tc.include("montiarc.generator.ma2jsim.component.features.FeatureField.ftl", feature)}

    ${tc.include("montiarc.generator.ma2jsim.component.features.FeatureGetter.ftl", feature)}
</#list>