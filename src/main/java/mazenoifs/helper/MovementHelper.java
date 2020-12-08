package mazenoifs.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mazenoifs.position.Coordinate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MovementHelper {

    @AllArgsConstructor
    public enum DIRECTION {
        NORTH(0), WEST(3), EAST(1), SOUTH(2);

        @Getter
        private final Integer direction;


        public static DIRECTION fromValue(Integer direction) {

            return Arrays.stream(DIRECTION.values())
                    .filter(dir -> dir.getDirection().equals(direction))
                    .findFirst()
                    .get();
        }
    }

    public static final Map<DIRECTION, DIRECTION> OPPOSITE_DIRECTIONS = new HashMap<>() {
        {
            put(DIRECTION.NORTH, DIRECTION.SOUTH);
            put(DIRECTION.SOUTH, DIRECTION.NORTH);
            put(DIRECTION.WEST, DIRECTION.EAST);
            put(DIRECTION.EAST, DIRECTION.WEST);
        }
    };

    public static Coordinate nextCoordinate(final Coordinate oldCoordinate, final MovementHelper.DIRECTION direction) {
        final var coordinate = new Coordinate(oldCoordinate.getX(), oldCoordinate.getY());
        coordinate.recalculate(direction);
        return coordinate;

    }
}
