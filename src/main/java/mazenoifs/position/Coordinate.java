package mazenoifs.position;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mazenoifs.helper.MovementHelper;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Coordinate {
    private Integer x, y;

    private static final Map<MovementHelper.DIRECTION, CoordinateCalculation> coordinateCalc = new HashMap<>() {{
        put(MovementHelper.DIRECTION.NORTH, (x, y) -> {
            y--;
            return new int[]{x, y};
        });
        put(MovementHelper.DIRECTION.EAST, (x, y) -> {
            x++;
            return new int[]{x, y};
        });
        put(MovementHelper.DIRECTION.SOUTH, (x, y) -> {
            y++;
            return new int[]{x, y};
        });
        put(MovementHelper.DIRECTION.WEST, (x, y) -> {
            x--;
            return new int[]{x, y};
        });
    }};

    public void recalculate(final MovementHelper.DIRECTION direction) {
        final var c = coordinateCalc.get(direction).calculate(x, y);
        x = c[0];
        y = c[1];
    }

}
