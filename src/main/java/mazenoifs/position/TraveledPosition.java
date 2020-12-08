package mazenoifs.position;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mazenoifs.helper.MovementHelper;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class TraveledPosition implements Position {
    private final Map<MovementHelper.DIRECTION, Position> positions;
    @Getter
    private final Coordinate coordinate;

    public TraveledPosition(final Coordinate coordinate, final Map<Integer, Integer> wallDistances) {
        this.coordinate = coordinate;
        positions = new HashMap<>() {{
            put(MovementHelper.DIRECTION.NORTH, new PossiblePosition());
            put(MovementHelper.DIRECTION.EAST, new PossiblePosition());
            put(MovementHelper.DIRECTION.SOUTH, new PossiblePosition());
            put(MovementHelper.DIRECTION.WEST, new PossiblePosition());
        }};

        wallDistances
                .entrySet()
                .stream()
                .filter(tuple -> tuple.getValue() == 0)
                .map(Map.Entry::getKey)
                .forEach(key -> positions.replace(MovementHelper.DIRECTION.fromValue(key), new WallPosition()));
    }

    public void addPosition(final TraveledPosition traveledPosition, final MovementHelper.DIRECTION direction) {
        traveledPosition.addPosition(this, MovementHelper.OPPOSITE_DIRECTIONS.get(direction));
        positions.replace(direction, traveledPosition);
    }

    public List<List<MovementHelper.DIRECTION>> getNextDirectionSequences() {
        return positions.entrySet().stream()
                .map((set) -> {
                    final var d = new ArrayList<MovementHelper.DIRECTION>();
                    set.getValue().getNextDirections(set.getKey(), this, d);
                    return d;
                })
                .sorted((p, n) -> p.size() > n.size() ? 1 : 0)
                .collect(Collectors.toList());
    }

    public void getNextDirections(final MovementHelper.DIRECTION direction, final Position prevPosition, final List<MovementHelper.DIRECTION> directions) {

        final var sequences = positions.entrySet().stream()
                .filter(s -> s.getKey() != MovementHelper.OPPOSITE_DIRECTIONS.get(direction))
                .map((set) -> {
                    final var d = new ArrayList<MovementHelper.DIRECTION>();
                    d.add(direction);
                    set.getValue().getNextDirections(set.getKey(), this, d);
                    return d;
                })
                .sorted((p, n) -> p.size() > n.size() ? 1 : 0)
                .collect(Collectors.toList());

        directions.addAll(sequences.get(0));
    }

}
