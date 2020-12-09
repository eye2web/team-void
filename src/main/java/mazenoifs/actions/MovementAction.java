package mazenoifs.actions;

import lombok.AllArgsConstructor;
import mazenoifs.Client;
import mazenoifs.Game;

import java.util.HashMap;

@AllArgsConstructor
public class MovementAction implements Action {
    private final int direction;

    @Override
    public void doAction() {
        final Client client = Game.client;
        final var directionRequest = new HashMap<String, Object>();
        directionRequest.put("playerId", Game.playerId);
        directionRequest.put("direction", direction);
        final var delay = client.move(directionRequest).get("frameTimeMilliseconds");

        System.out.printf("Moving into %d direction%n", direction);

        try {
            Thread.sleep(delay + 500);
        } catch (InterruptedException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
