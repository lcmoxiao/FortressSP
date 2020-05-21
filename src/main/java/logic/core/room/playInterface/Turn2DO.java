package logic.core.room.playInterface;

import logic.beans.Occupation;
import logic.core.room.MapInfo;

import java.util.List;

public interface Turn2DO {

    Occupation getHumanByName(String name);
    void produceCrops(Occupation human, int row);
    void produceCrops(List<Occupation> human, int row);
    void addOneMiner();
    MapInfo.Group[][] getWarInfo();

}
