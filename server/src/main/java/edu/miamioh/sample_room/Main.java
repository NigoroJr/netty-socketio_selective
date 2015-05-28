package edu.miamioh.sample_room;

import java.util.*;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;

/**
 * Sample program to test the functionality of rooms using netty-socketio
 *
 */
public class Main {
    public static final String SERVER_HOSTNAME = "localhost";
    public static final int SERVER_PORT = 3000;
    public static final String TEAM_SELECT = "team_select";
    public static final String BROADCAST_REQUEST_TEAM = "bcast_req_team";
    public static final String BROADCAST_REQUEST_WORLD = "bcast_req_world";
    public static final String BROADCAST_TEAM = "bcast_team";
    public static final String BROADCAST_WORLD = "bcast_world";

    public static final HashMap<String, ArrayList<DataObj>> teams =
        new HashMap<String, ArrayList<DataObj>>();;

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname(SERVER_HOSTNAME);
        config.setPort(SERVER_PORT);

        final SocketIOServer server = new SocketIOServer(config);

        // When client selects the team it belongs to
        DataListener<DataObj> onTeamSelect = new DataListener<DataObj>() {
           @Override
           public void onData(SocketIOClient client, DataObj data,
                   AckRequest ack) {
               String client_team = data.team;
               // Create new team
               if (!teams.containsKey(client_team)) {
                   teams.put(client_team, new ArrayList<DataObj>());
                   server.addNamespace(client_team);
               }

               teams.get(client_team).add(data);
               client.joinRoom(client_team);

               System.out.printf("Client %s has selected team %s\n",
                       data.userName, data.team);
           }
        };

        // "Broadcast to team" request handler
        DataListener<DataObj> onBroadcastTeam = new DataListener<DataObj>() {
            @Override
            public void onData(SocketIOClient client, DataObj data,
                    AckRequest ack) {
                server.getRoomOperations(data.team)
                    .sendEvent(BROADCAST_REQUEST_TEAM, data);
                // Or,
                //server.getNamespace(data.team).getBroadcastOperations()
                //    .sendEvent(BROADCAST_REQUEST_TEAM, data);

                System.out.printf("TEAM: %s (%s) => [%s]\n", data.userName, data.id, data.team);
                System.out.printf("    '%s'\n", data.message);
            }
        };

        // "Broadcast to world" request handler
        DataListener<DataObj> onBroadcastWorld = new DataListener<DataObj>() {
            @Override
            public void onData(SocketIOClient client, DataObj data,
                    AckRequest ack) {
                server.getBroadcastOperations().sendEvent(
                        BROADCAST_REQUEST_WORLD, data);

                System.out.printf("WORLD: %s (%s) => [WORLD]\n", data.userName, data.id);
                System.out.printf("    '%s'\n", data.message);
            }
        };

        // Add event listeners
        server.addEventListener(TEAM_SELECT, DataObj.class, onTeamSelect);
        server.addEventListener(BROADCAST_REQUEST_TEAM, DataObj.class, onBroadcastTeam);
        server.addEventListener(BROADCAST_REQUEST_WORLD, DataObj.class, onBroadcastWorld);

        server.start();
        System.out.print("Server started on " + SERVER_HOSTNAME);
        System.out.println(" port " + SERVER_PORT);
        System.out.println("Enter 'q' to quit");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.nextLine().equals("q")) {
                break;
            }
        }
        scanner.close();

        System.out.println("Okay, stopping server...");
        server.stop();
        System.out.println("Server has stopped");
    }
}
