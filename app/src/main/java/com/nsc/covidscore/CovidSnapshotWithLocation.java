package com.nsc.covidscore;

// this is a class to represent the relation between Location and CovidSnapshot

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CovidSnapshotWithLocation {
    @Embedded
    Location location;
    @Relation(
            parentColumn = "id",
            entityColumn = "location_id"
    )
    public List<CovidSnapshot> covidSnapshotList;
}
