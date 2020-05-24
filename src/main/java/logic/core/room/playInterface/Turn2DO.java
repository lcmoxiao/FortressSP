package logic.core.room.playInterface;

import logic.beans.Occupation;
import logic.core.room.Group;

import java.util.List;

public interface Turn2DO {

    Occupation getHumanByName(String name);

    Occupation getHumanById(int id);

    boolean produceCrops(Occupation human, int row);

    boolean produceCrops(List<Occupation> human, int row);

    void randomProduce(int row);

    void addOneMiner();

    Group[][] getWarInfo();


}
