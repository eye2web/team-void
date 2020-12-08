package mazenoifs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
public class Status {

    private Integer[] wallDistances;
    private List<Entity> entities;


    public Map<Integer, Integer> getWallDistances() {

        return new HashMap<>() {{
            put(0, wallDistances[0]);
            put(1, wallDistances[1]);
            put(2, wallDistances[2]);
            put(3, wallDistances[3]);
        }};
    }
}
