package mazenoifs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {

    private int x, y, dir;

    public Position() {
        x = 0;
        y = 0;
        dir = 0;
    }

    public Position(Integer[] dir, Position prev) {

        x = prev.x - dir[1];
        x += dir[3];

        y = prev.y - dir[0];
        y += dir[2];
    }

    public void set(Integer[] dir) {
        x -= dir[1];
        x += dir[3];

        y -= dir[0];
        y += dir[2];
    }

    public boolean isSame(Position comparePos) {
        return x == comparePos.getX() && y == comparePos.getY();
    }

}
