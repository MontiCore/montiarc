/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/*
 * Valid model.
 */
component ConfigurableComponentWithInnerCfgComp(int myParam) {

    component Inner(int myParam) {
    }

    component Inner(myParam) myInner;
}
