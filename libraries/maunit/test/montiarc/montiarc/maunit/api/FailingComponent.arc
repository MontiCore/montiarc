/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

<<test, ticks=[1,2,3], exception=[null, null, java.lang.AssertionError.class]>>
component FailingComponent() {
  int i = 0;
  compute {
    i = i + 1;
    if (i >= 3) {
      montiarc.maunit.api.Assertions.fail();
    }
  }
}
