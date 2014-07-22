# :midi-seq
A polymetric midi sequencer written in Clojure using Quil and Overtone to drive the Roland TR-8 drum machine. This hack is creating during the ClojureHackBus ride on the way from Berlin to EuroClojure Krakow.

Click to toggle the buttons to trigger the drums. Hold any key plus clicking a button will alter the amount of steps for a certain instrument creating more complex drum patterns.

![Screenshot](http://cl.ly/image/0m383a0n3647/Screen%20Shot%202014-06-26%20at%2012.19.26.png)

## Using it
To install:
```
brew install leiningen
lein run
```

If you don't have a TR-8 around, but happen to run OS X you can simply use OS X's native virtual midi routing (IAC Driver) and pipe it into your favourite soft drums.


