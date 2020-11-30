package com.nsc.covidscore.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RoomHelpers {
    /**
     * Used by CovidSnapshotWithLocationViewModel to set the mutable CovidSnapshot with new API info
     * @param mcs the mutable CovidSnapshot to set
     * @param snapshot the temporary CovidSnapshot used to assemble the API info
     */
    public static void setSnapshot(MutableLiveData<CovidSnapshot> mcs, CovidSnapshot snapshot) {
        if (snapshot.hasFieldsSet()) {
            mcs.setValue(snapshot);
        }
    }

    /**
     * Used by CovidSnapshotWithLocationRepository to determine whether a new CovidSnapshot should
     * be inserted into the database
     * @param currentSnapshot the current CovidSnapshot being observed
     * @param newSnapshot the new snapshot to possibly enter into the database
     * @return true if the new snapshot is a new unique location, else false
     */
    public static boolean shouldInsertSnapshot(LiveData<CovidSnapshot> currentSnapshot, CovidSnapshot newSnapshot) {
        return currentSnapshot.getValue() == null || !currentSnapshot.getValue().hasSameData(newSnapshot) && newSnapshot.getLocationId() != null;
    }
}
