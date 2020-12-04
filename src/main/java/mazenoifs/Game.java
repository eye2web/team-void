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

    private List<Position> previousPos;

    private final Client client;

    private boolean botRunning;
    private Map<String, String> player;

    private final Position currPos;

    public Game() {
        clientId = "WeAreTheVoidResistenceIsFutile";

        previousPos = new ArrayList<>();

        movements = new HashMap<>();
        movements.put(0, new Integer[]{1, 0, 0, 0});
        movements.put(1, new Integer[]{0, 1, 0, 0});
        movements.put(2, new Integer[]{0, 0, 1, 0});
        movements.put(3, new Integer[]{0, 0, 0, 1});

        botRunning = true;

        client = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .target(Client.class, "https://dojomaze-api.maas.codes");

        currPos = new Position();
        previousPos.add(new Position());
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

            System.out.println(String.format("Status %s", status));

            final var wallDistances = status.getWallDistances();


            // ---xppp
            // nnn-nnn

            // new Position(wallDistances.toArray(new Integer[wallDistances.size() - 1]), currPos);
            final var posibilities = createFuturePositions(currPos);

            final var actualPosibilities = posibilities.stream().filter(posibility ->
                    previousPos.stream().noneMatch(prevPos ->
                            (prevPos.getX() == posibility.getX() && prevPos.getY() == posibility.getY())
                    )
            ).collect(Collectors.toList());

            final List<Integer[]> wallDistList = new ArrayList<>();

            wallDistList.add(new Integer[]{0, wallDistances.get(0)});
            wallDistList.add(new Integer[]{1, wallDistances.get(1)});
            wallDistList.add(new Integer[]{2, wallDistances.get(2)});
            wallDistList.add(new Integer[]{3, wallDistances.get(3)});

            // can fail due to null options for actualPosibilities
            final var nextDirection = wallDistList.stream()
                    .filter(wallDistance -> wallDistance[1] > 0)
                    .filter(walld ->
                            actualPosibilities.stream().anyMatch(position -> position.getDir() == walld[0])
                    ).map(w -> w[0]).findAny();

            nextDirection.ifPresentOrElse(next -> {
                        currPos.set(movements.get(next));
                        previousPos.add(new Position(currPos.getX(), currPos.getY(), 0));

                        final var directionRequest = new HashMap<String, Object>();
                        directionRequest.put("playerId", player.get("playerId"));
                        directionRequest.put("direction", next);

                        System.out.println(String.format("Move to %d", next));
                        final var delayInMilli = client.move(directionRequest).get("frameTimeMilliseconds");
                        System.out.println(String.format("Sleep %d", delayInMilli));


                    }
                    , () -> {
                        System.out.println("Removing 2 last positions");
                        previousPos = previousPos.subList(0, previousPos.size() - 2);
                        previousPos.add(currPos);
                    });
            Thread.sleep(3000);

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
