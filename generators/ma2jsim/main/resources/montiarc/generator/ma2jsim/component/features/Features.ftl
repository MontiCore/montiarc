<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#list helper.getFeatures(ast) as feature>
    ${tc.include("montiarc.generator.ma2jsim.component.features.FeatureField.ftl", feature)}

    ${tc.include("montiarc.generator.ma2jsim.component.features.FeatureGetter.ftl", feature)}
</#list>