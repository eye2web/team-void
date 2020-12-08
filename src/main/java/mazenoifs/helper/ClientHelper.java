package mazenoifs.helper;

import feign.FeignException;
import lombok.SneakyThrows;
import mazenoifs.Game;
import mazenoifs.Status;

public class ClientHelper {

    @SneakyThrows
    public static Status getStatus() {
        while (true) {
            try {
                return Game.client.status(Game.player);
            } catch (FeignException ex) {
                System.out.println(ex.getLocalizedMessage());
                Thread.sleep(500);
            }
        }
    }
}
