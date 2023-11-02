<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component PedestrianCrossing {
  port in boolean carDetected,
	     in ButtonEvent pedestrianCrossingRequest,
       out Color carTrafficLightColor,
       out Color pedestrianTrafficLightColor;

  automaton {

    initial / {
      carTrafficLightColor = Color.RED;
      pedestrianTrafficLightColor = Color.RED;
    } state CarsPass {
      entry / {
        carTrafficLightColor = Color.GREEN;
        pedestrianTrafficLightColor = Color.RED;
      }
      do / {
        carTrafficLightColor = Color.GREEN;
        pedestrianTrafficLightColor = Color.RED;
      }
    };

    state PassengersPass {
      entry / {
        pedestrianTrafficLightColor = Color.GREEN;
        carTrafficLightColor = Color.RED;
      }
      do / {
        pedestrianTrafficLightColor = Color.GREEN;
        carTrafficLightColor = Color.RED;
      }
    }; 
  }

  CarPass -> PassengerPass [!carDetected && pedestrianCrossingRequest == ButtonEvent.PRESSED];
  PassengerPass -> CarPass [carDetected];    
}
```
Automatons allow the description of state-based behavior.
One can declare actions to be executed when a state is entered, while it is 
maintained, when it is left, or when specific transitions are taken.
Moreover, one can define initially executed behavior in the initial state 
declaration between the `initial` keyword and the initial state name.

