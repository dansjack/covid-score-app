//package com.nsc.covidscore.room;
//
//// this is a class to represent the relation between Location and CovidSnapshot
//
//import androidx.room.Embedded;
//import androidx.room.Relation;
//
//import com.nsc.covidscore.room.CovidSnapshot;
//import com.nsc.covidscore.room.Location;
//
//import java.util.List;
//
//public class CovidSnapshotWithLocation {
//    @Embedded
//    Location location;
//    @Relation(
//            parentColumn = "location_id",
//            entityColumn = "covid_snapshot_id"
//    )
//    public List<CovidSnapshot> covidSnapshotList;
//    public void setCovidSnapshotList(List<CovidSnapshot> covidSnapshotList) { this.covidSnapshotList = covidSnapshotList; }
//}
