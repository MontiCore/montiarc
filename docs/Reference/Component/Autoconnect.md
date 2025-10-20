# Autoconnect

MontiArc allows to automatically complete component definitions by adding [connectors](./Connectors.md) based on the present ports.
Different strategies exist for selecting which ports to connect. These are: `off`, `port`, and `type`. 

!!! info 
    It is **not** possible to use multiple autoconnect modes in a component.


## Strategies

### Autoconnect Off
The default strategy. It does not add any new connectors and can be omitted. 


```montiarc
component Comp {
  autoconnect off;
  port in int i; 
  port out boolean o;
  
  component Inner inner {
      port in int i;
  }
}
```

### Autoconnect Port
If ports with matching names and compatible types exist, a connection is added.
If multiple ports match, no connector is added for the corresponding ports.

##### 1. Example
Since there is no matching port of the same type and name, no connector is added.
```montiarc
component Comp {
  autoconnect port;
  port in int i;
  port out boolean o;

  component Inner inner {
    port in int i_inner;
  }
}
```

##### 2. Example
Adds the connectors `i -> inner.i` and `o.inner -> o`.
```montiarc
component Comp {
  autoconnect port;
  port in int i;
  port out boolean o;
  
  component Inner inner {
    port in int i;
    port out boolean o;
  }
}
```

##### 3. Example
Two connectors are added: `i -> inner1.i` and `i -> inner2.i`.
For the outgoing port `o` multiple connections are possible, `inner1.o -> o` and `inner2.0 -> o`, but since it can only be connected to one, no connection is added.
```montiarc
component A {
  autoconnect port;
  port in int i;
  port out int o;
  
  component Inner inner1, inner2 {
    port in int i;
    port out int o;
  }
}
```
   

### Autoconnect Type
If ports with compatible types exist, a connection is added.
If multiple ports match, no connector is added for the corresponding ports.

##### 1. Example
Adds the connector `i -> inner.i_inner` since the type is the same.
```montiarc
component Comp {
  autoconnect type;
  port in int i;

  component Inner inner {
    port in int i_inner;
  }
}
``` 

##### 2. Example
Adds the connector `i -> inner.i_inner` and `inner.o -> o` since the type is the same.
```montiarc
component Comp { 
  autoconnect type;
  port in int i;
  port out boolean o;
  
  component Inner inner {
    port in int i_inner;
    port out boolean o; 
  }
}
```

##### 3. Example
No connector is added, since multiple ambiguous candidates exist: `i -> inner.i_inner`, `inner.o -> o`, `inner.o -> inner.i_inner` and `o -> i`.
```montiarc
component Comp {
  autoconnect type;
  port in int i;
  port out int o;

  component Inner inner {
    port in int i_inner;
    port out int o;
  }
}
``` 

