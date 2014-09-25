Starting the framework:
-----------------------
(e.g. in Eclipse)  Make a new Eclipse project with build path "CFour"

Start the GUI: src/gui/C4Frame.java ' Run as Java Application (Alt-Shift-X, then J)

Configuration: There are some parameter files with useful agent settings in tdpar/, load them with Params - Open

Help files are in CFour/src/doc: index.htm and Help.pdf


Issues: 
-------

When training a CFour net, there can be a heap-memory crash.
How to cure: Set this option in "Run Dialog - Arguments - VM-Arguments"
	-Xmx512M
then the program gets 512 MB heap space and the error is gone.


