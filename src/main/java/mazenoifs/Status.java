package mazenoifs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class Status {

    private List<Integer> wallDistances;
    private List<Entity> entities;

}
