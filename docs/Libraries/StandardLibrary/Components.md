<!-- (c) https://github.com/MontiCore/monticore -->
# Components

##### montiarc.lang

<div class="grid cards" markdown>
-   [Delay<T\>](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/Delay.arc)

    ---

    Delays all inputs by one tick.
    

-   [TSDelay<T\>(T initialValue)](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/TSDelay.arc)

    ---

    Time synchronous version of the delay component.

-   [ToSyncFirstIn<T\>](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/ToSyncFirstIn.arc)

    ---

    Converts an event stream into a synchronous stream by discarding all events following the first received one.

-   [ToSyncLastIn<T\>](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/ToSyncLastIn.arc)

    ---

    Converts an event stream into a synchronous stream by discarding all events except the last received one.

-   [DynamicTimer](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/DynamicTimer.arc)

    ---

    Sends a signal once the received duration has elapsed.

-   [ConstTimer](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/ConstTimer.arc)

    ---

    Sends a signal once the constant duration has elapsed. Receives only a start signal.

</div>

---

##### montiarc.lang.logic

A large library of logic gate components for circuit and gate modeling.

[:octicons-arrow-right-24: Read more](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/logic)

---

##### montiarc.lang.math

A library of synchronous math functions for integer and long values.

[:octicons-arrow-right-24: Read more](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/logic)

---

##### montiarc.lang.string

<div class="grid cards" markdown>
-   [ToString](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/string/ToString.arc)

    ---

    Function component emitting the input converted to string.
    

-   [ChartAt(char default)](https://github.com/MontiCore/montiarc/blob/dev/libraries/montiarc-base/main/montiarc/montiarc/lang/string/CharAt.arc)

    ---

    Time-synchronous chartAt function component. Takes in a string and an index and emits the char at the index
    or `default` if the index is out of range.

</div>

---