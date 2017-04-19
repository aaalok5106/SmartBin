package com.mridul.smartbin.MakingPath;


import java.util.ArrayList;
import java.util.List;

import com.mridul.smartbin.MakingPath.Route;

/**
 * Created by Mridul on 05-03-2017.
 */

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route, int dist, int time, String origin_id_of_bin);

}
