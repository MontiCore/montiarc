/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model. Deploy components should neither have ports nor parameter.
 */
<<deploy>> component DeployComponentSignature (int value = 32) {

    port
        in Boolean i,
        in Boolean o;
}
