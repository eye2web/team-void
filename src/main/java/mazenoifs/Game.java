package mazenoifs;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import mazenoifs.helper.ClientHelper;
import mazenoifs.helper.MovementHelper;
import mazenoifs.actions.Action;
import mazenoifs.actions.MovementAction;
import mazenoifs.position.Coordinate;
import mazenoifs.position.Position;
import mazenoifs.position.TraveledPosition;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    public static final String CLIENT_ID = "WeAreTheVoidResistenceIsFutile";
    public static String playerId;
    public static Client client;
    public static Map<String, String> player;


    private final List<TraveledPosition> positionList;
    private TraveledPosition currentPosition;

    public Game() {
        client = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .target(Client.class, "https://dojomaze-api.maas.codes");

        positionList = new ArrayList<>();
    }

    public void start() {

        // Create new player
        final var newPlayer = new HashMap<String, String>();
        newPlayer.put("name", "DoYouKnowTheWay");
        newPlayer.put("clientId", CLIENT_ID);
        player = client.newPlayer(newPlayer);
        playerId = player.get("playerId");

        final var status = ClientHelper.getStatus();
        currentPosition = new TraveledPosition(new Coordinate(0, 0), status.getWallDistances());
        positionList.add(currentPosition);
        while (true) {
            executeActions(updateActions());
        }
    }

    private List<Map.Entry<MovementHelper.DIRECTION, Action>> updateActions() {
        final var directionSequences = currentPosition.getNextDirectionSequences();

        return directionsToActions(directionSequences.get(0));
    }

    private void executeActions(final List<Map.Entry<MovementHelper.DIRECTION, Action>> actions) {
        actions.forEach(action -> {
            action.getValue().doAction();
            final var status = ClientHelper.getStatus();

            final var newCoordinate = new Coordinate(currentPosition.getCoordinate().getX(), currentPosition.getCoordinate().getY());
            newCoordinate.recalculate(action.getKey());

            currentPosition = positionList.stream()
                    .filter(p -> p.getCoordinate().equals(newCoordinate))
                    .findFirst()
                    .orElseGet(() -> {
                        final var newPosition = new TraveledPosition(newCoordinate, status.getWallDistances());
                        currentPosition.addPosition(newPosition, action.getKey());
                        newPosition.addPosition(currentPosition, MovementHelper.OPPOSITE_DIRECTIONS.get(action.getKey()));
                        return newPosition;
                    });
        });
    }

    private List<Map.Entry<MovementHelper.DIRECTION, Action>> directionsToActions(final List<MovementHelper.DIRECTION> directions) {
        return directions.stream().map(direction ->
                new AbstractMap.SimpleEntry<MovementHelper.DIRECTION, Action>(direction,
                        new MovementAction(direction.getDirection()))
        ).collect(Collectors.toList());
    }


}
