# Tanks! 2D User manual 

## First start:

After downloading and extracting JAR, the game is ready to launch. Launch the app by opening terminal in the folder where you extracted your JAR and type in:<br>
`java fel.cvut.pjv.Main` <br>
This will take you to the Main Menu, where you will see the provided:
- Example level
- Example player
- Empty scoreboard

It is possible to start playing immediately after selecting the example level and player and clicking on [Start Game].

## Controls

### Movement

Tank controls:
- W -> move up
- A -> move left
- S -> move down
- D -> move right
- SPACEBAR -> shoot

Level controls:
- Esc key -> pause level and bring up quit or continue options

## Game mechanics and goal
Move on the board, pick up power-ups and shoot enemy tanks. But watch out for enemy shots! Bullets explode on impact or after a certain distance, so don't get caught in the blast. <br><br>
Make sure to get to the power-ups quickly, as a single hit will destroy them!<br><br>
To stay alive, hide behind rocks, which provide a impenetrable barrier against all incoming shots. But careful! They will not protect you from the explosion if it's large enough.<br><br>
Hitting an enemy with your own bullet will give you 100 score and destroying one will give you 500. Destroy all enemies to complete the level. <br><br>
If you wish to quit a level before completing it, just hit ESC and click on [Save and quit level]<br>
This will save your current health, armor and damage so you can try again when you want to. But you will not get to keep your score.<br><br>
If you get destroyed, you will get returned to the Main Menu with the same stats you had when you entered the level, score will be also lost.

## Creating custom levels and players

### **Important information**

The {} brackets are there only to signify that it should be replaced by a valid value of your choice. When editing, make sure **not** to leave them in.<br>
Example of **correct** entity: `enemytank 0 0 up 10 5 15 8 1`<br>
Example of **incorrect** entity: `enemytank {0} {0} {up} {10} {5} {15} {8} {1}`

### Creating players:

There are two options when it comes to creating players. Either you can click on [Create Player] in Main Menu or you can create the file manually inside the Players directory. Even if you want fully custom player stats, it is recommended to use [Create Player] first and then modify it to ensure there will not be any duplicate names or wrong file types. Also it is easier to just replace existing values.<br>
When you open a player-file you will see something like:<br>
`playertank 0 0 up 5 6 7 8 1`<br>
What can be edited are the values that come after `up`. Everything before and included is there for the game to load correctly. <br>
The values meanings are:<br>
`playertank 0 0 up {health} {armor} {range} {damage} {explosion radius}`<br>
**All values should be positive, whole numbers** separeted by a single space.<br>
Be careful when setting explosion radius, the size grows quickly. Value 0 will explode only the tile where bullet hit something, value 1 will create a 3 by 3 explosion.<br>
Name of the file will be the name of the player displayed in game. It should not contain any whitespaces or special characters.

### Creating levels:

To create a level, create a text file in the Levels directory. Name of the file will be the name of the level displayed in game. <br>
You have access to 20 by 20 game-board where you can add what you like. We're all programmers here so **positions start at 0** and end at 19.<br>
The formatting is is one entity per line.<br>
The available entities are:
- playertank
- enemytank
- rock
- firepowerup
- healthup
- armorup
- rock
- mine

#### Facing direction of entities

You can set which direction will be the player and enemy tanks facing when spawned.<br>
The options are:
- `up`
- `down`
- `left`
- `right`

Entities other than enemy tanks or players should be always used with `up`, any other choice will be defaulted to `up` during level generation anyway.

##### Player tank

Determines where the player will spawn. Make sure you put it only once in the file. Only first one will be generated. <br>
The syntax for Player Tank is:<br>
`playertank {row position} {column position}` <br>
The values should be within the permitted game-board positions, separated by a single space.

##### Enemy tank

Creates an enemy. You can create as many as you like, but higher numbers may get chaotic. Each enemy makes their own moves and shots. <br>
The syntax for Enemy tank is:<br>
`enemytank {row position} {column position} {facing direction} {health} {armor} {range} {damage} {explosion radius} <br>`
All values should be positive whole numbers separated by a single space. Careful when choosing 
explosion radius, the size grows quickly.

##### Rock

Creates environmental barrier which can not be passed through by tanks nor bullets.<br>
The syntax for Rock is:<br>
`rock {row position} {column position}`<br>
All values should be positive whole numbers separated by a single space.

##### Power ups

Creates a power up which increments the parameter after which its called. Both player and enemy tanks are able to pick them up. <br>
The options for start of line tags are:
- healthup
- armorup
- firepowerup

The syntax for Power ups is:<br>
`{powerup name} {row position} {column position} {increment}`<br>
All values should be positive whole numbers separated by a single space.

##### Mine 

Creates a mine that explodes when driven on by any tank. Can be destroyed by shooting it.<br>
The syntax for Mine is:<br>
`mine {row position} {column position} {damage} {explosion radius}`<br>
All values should be positive whole numbers separated by a single space. Careful when choosing explosion radius, the size grows quickly.

## Possible issues

Any issues you encounter will be most likely caused by a syntax mistake in a level file or a player file. If something is not working correctly, check the terminal from which you launched the game for a log of the possible cause of the issue! <br>
Possible warnings or errors are:
- Error during loading player values due to issues with file {filename} - player file is corrupted / incorrect.
- Error during gameBoard setup due to issues with file {filename} - game failed to open level file
- Failed to parse any entity - make sure level file is correct - incorrect entity tag at line start.
- Position {row} {column} is already occupied - setting an entity to already full position
- Player can not be set on occupied position - fatal, level can not start without a player and player cant be set on an occupied positon
- Attempted to set content out of gameBoard at pos {row} {column} - either row or column is out of bound.

If the issue persists do not hesitate to contact me!


# Technical documentation of Tanks! 2D

## Used Technologies
- Version control - Gitlab
- Java version - Java 11
- Build system - Maven
- GUI - Java Swing
- IDE - Intellij IDEA ultimate edition

## Project Structure

Project uses MVC design pattern. It is divided into "Menu" part and "Game part", each having its own controller and view. 

### Menu

#### Menu Controller

After launching the application, an instance of Menu controller is created. Menu controller is responsible for initiating main menu GUI and responding to user actions. Menu controller also initiates Game controller. After user selects level and players and clicks [Start game], control is handed to Game Controller

#### Menu View

Menu view displays a scoreboard and lists of available levels and players. There are three buttons for the user to use. An [Exit game] button, a [Start Game] button and a [Create Player] button. A click on any of these buttons is relayed back to Menu Controller which then performs the correct action.


### Game 

#### Bullet Driver

When a bullet is created, it is submitted to bullet driver along with the time of its creation. Bullet drier than iterates through its queue of bullets and sends requests to game controller to move a specific bullet when the delay for that specific bullet has passed. Exploded bullets are promptly discarded.

#### Enemy Tank Driver

When setting game board an enemy tank driver is created along with enemy tank. That enemy tank is then passed to the driver which then submits movement or shooting requests to game controller after a set delay. Thread of enemy tank driver ends when the tanks HP get to 0 or when its terminated early by game controller (e.g. leaving the level). 

#### Game Controller

Handles changes to model. This includes but is not limited to moving, creating and removing entities. Also handles terminating a level based either on in-game events or user input.

##### Move vehicle

Takes vehicle to be moved and a direction in which the vehicle should be moved. If the direction of requested movement is the same as the direction which the vehicle is facing, the vehicle is rotated at the same tile. If it is matching it checks whether the requested position is within board bounds and if it can be moved on. If it can be moved and is not empty it activates the entity and then moves the vehicle there. Else it just moves the vehicle.

##### Move Bullet

Similar to move vehicle. Handles movement of bullets and check whether the bullet should be exploded either based on collision or on remaining range. Upon collision it calls private method ExplodeTiles which hits all entities in set radius around explosion positions.

#### Game View

Displays the current current state of game board. Its update method takes a list of positions where change occurred and which need to be redrawn. This ensures we do not perform unnecessary updates to view, which would slow down the app.  

#### Game Loop

Is set to run at 60fps. Is responsible for relaying user input to Game Controller to for requestion view updates. Checks whether the level should be terminated based on in-game events (all enemies destroyed / player destroyed).

### Model

Game state is kept as a 2D array of Board Tiles. Each board tile can contain one entity and/or one Explosion Tile at a time. Tiles are discrete and all interactions between their contents are handled by the Game Controller.

#### Board Tile

Smallest unit of game board. Can contain a single entity and an Explosion tile at the same time. This was chosen in order to be simply overlay entities with explosions in view without keeping original content temporarily stored. All methods are synchronized to ensure thread safety. This also keeps the application running fast as a queue forms only when two threads try to access the same instance of Board Tile instead of them waiting for access to Game Board as a whole.

#### Game Board

Acts as a middle man between Game Controller and individual instances of Board Tiles. Takes requests for setting/removing content. Also handles generation of a level from specified file. Levels are specified inside of a text file. One line with correct syntax (see user manual) corresponds to one entity created in level.

#### Game Entities

Game Entity is the parent class of all objects in game. <br>
Inheritance structure of game entities: <br>
![Entity Hiearchy](uploads/cf57085ba0607ed3af2f0821a53be683/package.png)

##### Powerups

Direct extension of game entity. They get activated by game controller when a vehicle drives on them. They increment a value after which they are named.

##### Explosives

Entities capable of damaging other entities on board. They create an explosion of set radius. Bullet is a movable explosive.

##### Explosion tile

Entity representing an explosion on the board. It gets passed to the getHit method of the entity is hit. Deals damage that got set by its creator.

##### Vehicles

Moveable entities that can shoot. Each vehicle is controlled either by enemy tank driver or by user. Player vehicle keeps its own score.

##### Rock

Indestructible environmental structure.
