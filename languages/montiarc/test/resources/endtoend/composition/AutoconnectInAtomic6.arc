/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The inner component is an atomic component that contains an autoconnect statement.
 */
component AutoconnectInAtomic6 {

  component Inner {
    autoconnect port;
  }

  Inner sub;

}
