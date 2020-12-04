package mazenoifs;

import feign.Headers;
import feign.RequestLine;

import java.util.HashMap;
import java.util.Map;

@Headers("Content-Type: application/json")
public interface Client {

    @RequestLine("POST /player/move")
    Map<String, Integer> move(HashMap<String, Object> direction);
 
    @RequestLine("POST /player/shoot")
    String shoot();

    @RequestLine("POST /player/status")
    Status status(final Map<String, String> player);

    @RequestLine("GET /dashboard/server")
    String serverDashboard();

    @RequestLine("POST /player/new")
    Map<String, String> newPlayer(final Map<String, String> newPlayer);
}
