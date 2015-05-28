# netty-socketio Selective broadcasting
## Description
Simple program that is meant for checking the functionalities of selective
broadcasting using [netty-socketio](https://github.com/mrniko/netty-socketio).

## How to use this
Compile the program with `mvn clean install` and run it with `mvn exec:java`.
You can quit the server by typing `q`. Open `client/index.html` from your
browser and enter the user name. Select the team you belong to, then the
group to broadcast to (world or team), and the message. The server will echo
back the message to the group.

## Limitations
Since this is just a sample program, there are some limitations and
assumptions.

* Server uses HTML tag ID as the team name
* Client only `hide()`s the buttons after team selection
* User can enter no user name when being prompted
* Uses jQuery?

## License
Same as netty-socketio

## Author
Naoki Mizuno
