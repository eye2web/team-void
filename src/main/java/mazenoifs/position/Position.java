package mazenoifs.position;

import mazenoifs.helper.MovementHelper;

import java.util.List;

public interface Position {
    void getNextDirections(final MovementHelper.DIRECTION direction, final Position prevPosition, final List<MovementHelper.DIRECTION> directions);
}
