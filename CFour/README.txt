Starting the framework:
-----------------------
(e.g. in Eclipse)  Make a new Eclipse project with build path "CFour".
It might be necessary to add two libraries to the project:
1) Right-Click on CFour - Properties - Java Build  Path - Libraries - Add JARs...
   CFour\lib\commons-compress-1.9\commons-compress-1.9.jar
2) Add JUnit 4 library with File - New - JUnit Test Case - Name TestFailure, 
   Superclass junit.framework.TestCase - Finish. Confirm the question "Add JUnit 4 to the project?"
   After this the class file TestFailure may be removed again.
	
Start the GUI: src/gui/C4Frame_v2_14.java (right-click) Run as Java Application (Alt-Shift-X, then J)

Configuration: There are some parameter files with useful agent settings in tdpar/, load them with Params - Open

Help files are in CFour/src/doc: index.htm and Help.pdf

agent/README.txt: Exporting a TD-agent in text format.


Issues: 
-------

When training a CFour net, there can be a heap-memory crash.
How to cure: Set this option in "Run Dialog - Arguments - VM-Arguments"
	-Xmx512M
then the program gets 512 MB heap space and the error is gone.


