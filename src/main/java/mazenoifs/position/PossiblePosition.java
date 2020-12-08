package mazenoifs.position;

import mazenoifs.helper.MovementHelper;

import java.util.List;

public class PossiblePosition implements Position {
    public void getNextDirections(final MovementHelper.DIRECTION direction, final Position prevPosition, final List<MovementHelper.DIRECTION> directions) {
        directions.add(direction);
    }
}
