# Timers
Countdown and Stopwatch timers, with some extra bits

Countdown and Stopwatch.
Played around with extra features
Attempts were made to make the user experience smooth
As well as make the code more easy to understand

Countdown - decided to let the user choose the values via NumberPickers, but only through scrolling them.
Can not start the timer without any values in them.
If time was already initiated and paused, the new values are then stored as the number picker values.
Also added an alarm, toggle on off, to let the user know that the time has run out.

<img src="https://github.com/squarePotatoe/Timers/assets/126165004/2499c925-21bd-42f5-a80f-3eb9b5fc77cc" width=300 height=500>
<img src="https://github.com/squarePotatoe/Timers/assets/126165004/25cfb61e-b200-47f6-b31b-d312e5fc3619" width=300 height=500>


Stopwatch - Has two timer:
1. Main timer, to show the total time elapsed
2. Secondary timer, to show the interval times

Timers are saved into a local Room database and displayed to the user in a live manner with a recycler view.
Database can be cleared manualy by the user with a reset button. By default any entries get cleared before new instance of a session.

<img src="https://github.com/squarePotatoe/Timers/assets/126165004/88a7d0e6-9769-4576-b97b-87c8ada21edd" width=300 height=500>
<img src="https://github.com/squarePotatoe/Timers/assets/126165004/e0878356-a064-4eee-a4b6-0340aec0cfc4" width=300 height=500>

A few things need to be tidied up still!
