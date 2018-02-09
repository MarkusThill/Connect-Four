Connect-Four
============

A Java framework for the game Connect Four (Connect-4) with different types of agents and algorithms (reinforcement learning [TD with eligibility traces], AlphaBeta-Search, MCTS). Contains a perfect-playing MiniMax agent for evaluation purposes.
The Connect-4 game playing framework (C4GPF) emerged from the work on several studies over the last years. The framework provides a GUI with which the user can train, interact with and measure the strength of various agents and more. The whole software is written in Java and can be easily extended by experienced programmers. 

## Authors
* Markus Thill (markus.thill "at" th-koeln.de) 
* Wolfgang Konen (wolfgang.konen "at" th-koeln.de)




## Features
* Direct interaction between user and agents: By playing against different agents, the user can get an own impression of the playing strength of the individual agents. 
* Animated matches between different agents: The user can select two agents and follow matches of these two agents against each other on the board. If desired, both agents interact automatically with each other and the user can watch the animated match. It is also possible to use a step-by-step mode in which the user decides when an agent performs the next move.
* Due to the fast inbuilt Minimax agent, the user can analyze the exact game-theoretic values and state-action values of arbitrary positions in a matter of a few milliseconds. 
* Benchmark options: It is possible to set up competitions between agents, where both opponents play a certain number of matches against each other (swapping the sides after each match if desired). After the competition is completed the user is provided with some statistics about the competition and the user can then also analyze individual matches. Another option is to use a simple benchmark for an agent, where the framework determines a scalar value, indicating the strength of the agent, based on a predefined number of matches against a perfect playing agent. This benchmark can also be used to estimate the strength of completely deterministic agents.
* Built-in Reinforcement Learning (RL) agent, as described below, which learns with n-tuple systems.
* Simple tools for visualizing, creating, adjusting and deleting n-tuples.
* GUI-based inspection of the look-up tables (LUTs) of the n-tuple system.
* Loading/saving of parameter files, which contain the exact settings of an agent. These files can be used in order to start a training process with pre-defined settings.
* Trained agents with all look-up tables and all other configurations can be saved to and loaded from small compressed files.
* Import/Export only the weights of an n-Tuple RL-Agent from/to a compressed ZIP file.
* Already included pre-trained agents and pre-defined parameter files.
* Help file that can be called from the GUI.

## Agents
The framework already provides several inbuilt agents which can be selected as opponents for the user or which can play matches against each other. It is fairly simple for other developers to plug in new agents, if the interface ``Agent.java'' is used. 
* Random Agent: Plays completely random moves and can be constantly defeated even by weak agents.
* Monte Carlo Tree Search (MCTS): A fairly simple agent in an early development phase, based on the general MCTS approach, without any special enhancements. With 500 000 iterations, the playing strength is comparable to a standard Minimax agent with a search depth of depth 12.
* Perfect playing Minimax agent supported by a 8-ply and 12-ply database and a transposition table (25 MB by default) and many other enhancements. Currently, this agent is one of the fastest tree-search algorithms available for Connect-4: Even without the support of the databases, the best move for the empty board can be found in less than 4 minutes on a Pentium-4 computer (using only a 25 MB transposition table). 
	The strength of the agent can be controlled by the search-depth. 
* Reinforcement Learning (RL) agent: The agent is based on an n-tuple system and trained with TD-Learning. The TD-Learning algorithm supports eligibility traces and can be augmented with several step-size adaptation algorithms, such as TCL, IDBD, Autostep and others (listed below).
* RL-Minimax agent: Performs a classical tree search. On the leafs of the tree an RL agent is used to estimate the corresponding state value.

## Step-size Adaptation Algorithms
All step-size adaptation algorithms that are described in this thesis can be selected and configured by the user in the C4GPF. At the time this work was created, several step-size adaptation algorithms are supported by our framework.


		
		includegraphics[width=0.99textwidth]{Figures.d/appendix/c4-main-window.png} 

		\includegraphics[width=0.79\textwidth]{Figures.d/appendix/tdl-parameters.png} 
	\caption[TD-parameter window of the \cfour Game Playing Framework]{TD-parameter window of the \cfour Game Playing Framework 
