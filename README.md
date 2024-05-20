
# **ODD OR EVEN**

This project is a simple odd or even game.
Each player is assigned with 'odd' or 'even'
Then the player chooses a number between 0 and 9, where the sum of the two numbers is calculated and checked if is odd or even.
The player that got the corresponding type of number wins the round.

The game is played in 5 rounds.

# **Project Description**

The goal of this project is create a simple client-server game. 

In order of the game to run, the server and client need to be constantly communicating. 

If the client goes down, then the server should be to detect that and logout the client and finish the game, giving the victory to the opponent. 

In the other hand, if, for some reason, the server goes down, both clients terminate their connection, and should reconect to the server, once it goes back up.


## **Run the project**

To run the project, first the server needs to be up.
It can be done running the project in the IntelliJ IDEA with the argument "8000" in the configuration.

In order to run the Project, in the Client Side you should compile the code and run as follows:
### **COMPILE**

```
javac -d ./out ./src/main/java/org/example/*.java
```

### **RUN**

```
java -cp ./out org.example.Client localhost 8000
```