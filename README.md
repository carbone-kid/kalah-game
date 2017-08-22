# This is a simple implementation of Kalah game 

It's made as a Spring Boot application using Spring MVC on the backend and AngularJS on the frontend.

It's wrapped to the Docker container, deployed to the cloud hosting and available here http://82.196.0.94/

The Docker container is available at https://hub.docker.com/ as sfirsov/kalah-game

As the game is multiplayer you first need to login with unique name.
![The login page](/screenshots/1.png)

Then you need to create a game...
![Creating the game](/screenshots/2.png)

... and waiting for the opponent.
![Waiting for the opponent](/screenshots/3.png)

Or join an existing game.
![Joining the game](/screenshots/4.png)

And finally play the game.
![Gaming](/screenshots/5.png)

There is a timeout for user inactivity. If the user inactive for some time of he will be removed. So if after some time AFK the game stopped working this is a feature, not a bug :)
