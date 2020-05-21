package logic.core.room.playInterface;

import logic.beans.Occupation;

public interface Turn1DO {

    Occupation getOccupationByName(String name);
    boolean addHumanToCorps(String name);
    boolean removeHumanFromCorps(String name);

}
