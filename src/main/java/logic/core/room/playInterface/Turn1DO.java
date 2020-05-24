package logic.core.room.playInterface;

import logic.beans.Occupation;

public interface Turn1DO {

    public Occupation getOccupationByName(String name);

    public boolean addHumanToCorps(String name);

    public boolean removeHumanFromCorps(String name);

}
