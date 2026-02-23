package world;

import location.Location;
import java.util.*;

public class World {

    private final List<Location> locations = new ArrayList<>();

    public void addLocation(Location loc){
        locations.add(loc);
    }

    public List<Location> getLocations(){
        return locations;
    }

}
