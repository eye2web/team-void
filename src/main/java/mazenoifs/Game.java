package mazenoifs;

import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final String clientId;
    private final Map<Integer, Integer[]> movements;
    private final Map<Integer, Integer> oppositeDirection;


    private List<Position> previousPositionList;

    private final Client client;

    private boolean botRunning;
    private Map<String, String> player;

    private final Position currentPosition;

    private int historyIndex;
    private int delay;

    public Game() {
        clientId = "WeAreTheVoidResistenceIsFutile";
        historyIndex = 0;
        previousPositionList = new ArrayList<>();

        movements = new HashMap<>();
        movements.put(0, new Integer[]{1, 0, 0, 0});
        movements.put(1, new Integer[]{0, 1, 0, 0});
        movements.put(2, new Integer[]{0, 0, 1, 0});
        movements.put(3, new Integer[]{0, 0, 0, 1});

        oppositeDirection = new HashMap<>();
        oppositeDirection.put(0, 2);
        oppositeDirection.put(1, 3);
        oppositeDirection.put(2, 0);
        oppositeDirection.put(3, 1);

        botRunning = true;

        client = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .target(Client.class, "https://dojomaze-api.maas.codes");

        currentPosition = new Position();
        previousPositionList.add(new Position());
    }

    @SneakyThrows
    public void start() {

        // Create new player

        final var newPlayer = new HashMap<String, String>();
        newPlayer.put("name", "Trex");
        newPlayer.put("clientId", clientId);
        player = client.newPlayer(newPlayer);

        while (botRunning) {

            Status status = null;
            boolean hasStatus = false;
            while (!hasStatus) {
                try {
                    status = client.status(player);
                    hasStatus = true;
                } catch (FeignException ex) {
                    Thread.sleep(500);
                }
            }

            //System.out.println(String.format("Status %s", status));

            final var wallDistances = status.getWallDistances();

            final List<Integer[]> wallDistList = new ArrayList<>();
            wallDistList.add(new Integer[]{0, wallDistances.get(0)});
            wallDistList.add(new Integer[]{1, wallDistances.get(1)});
            wallDistList.add(new Integer[]{2, wallDistances.get(2)});
            wallDistList.add(new Integer[]{3, wallDistances.get(3)});

            // new Position(wallDistances.toArray(new Integer[wallDistances.size() - 1]), currPos);
            final var possibilities = createFuturePositions(currentPosition);

            final var actualPossibilities = possibilities.stream().filter(possibility ->
                    previousPositionList.stream().noneMatch(prevPos -> prevPos.isSame(possibility))
            ).filter((poss) ->
                    wallDistList.stream().anyMatch((wallDist) -> wallDist[1] > 0 && wallDist[0] == poss.getDir())
            ).collect(Collectors.toList());


            actualPossibilities.stream()
                    .map(Position::getDir)
                    .map(Object::toString)
                    .reduce((acc, value) -> acc += " " + value).ifPresent(positions ->
                    System.out.printf("Possible directions: (%s)%n", positions)
            );

            Collections.shuffle(actualPossibilities);
            final var nextDirection = actualPossibilities.stream().findFirst();

            nextDirection.ifPresentOrElse(next -> {
                        historyIndex = 2;
                        currentPosition.set(movements.get(next.getDir()));
                        currentPosition.setDir(next.getDir());

                        previousPositionList.add(new Position(currentPosition.getX(), currentPosition.getY(), currentPosition.getDir()));

                        final var directionRequest = new HashMap<String, Object>();
                        directionRequest.put("playerId", player.get("playerId"));
                        directionRequest.put("direction", next.getDir());

                        System.out.printf("Move to %d%n", next.getDir());
                        delay = client.move(directionRequest).get("frameTimeMilliseconds");
                        //System.out.println(String.format("Sleep %d", delayInMilli));
                    }
                    , () -> {

                        final var prevPos = previousPositionList.get(previousPositionList.size() - historyIndex);

                        final var oppositeDir = oppositeDirection.get(prevPos.getDir());
                        currentPosition.setDir(oppositeDir);
                        currentPosition.setX(prevPos.getX());
                        currentPosition.setY(prevPos.getY());

                        System.out.printf("Moving to history position. direction: (%d)%n", oppositeDir);
                        final var directionRequest = new HashMap<String, Object>();
                        directionRequest.put("playerId", player.get("playerId"));
                        directionRequest.put("direction", oppositeDir);
                        delay = client.move(directionRequest).get("frameTimeMilliseconds");

                        //System.out.println(String.format("Sleep %d", delayInMilli));
                        historyIndex++;
                    });
            Thread.sleep(delay);

        }
    }


    public List<Position> createFuturePositions(final Position current) {

        final var result = new ArrayList<Position>();
        result.add(new Position(current.getX(), current.getY() - 1, 0));
        result.add(new Position(current.getX() - 1, current.getY(), 1));
        result.add(new Position(current.getX(), current.getY() + 1, 2));
        result.add(new Position(current.getX() + 1, current.getY(), 3));
        return result;
    }

}
