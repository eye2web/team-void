package mazenoifs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Entity {

    private String visual;
    private String name;
    private List<Integer> distances;

}
