//package com.nsc.covidscore.room;
//
//import com.nsc.covidscore.Constants;
//
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//
//public class EntityListener implements PropertyChangeListener {
//
//    // CovidSnapshot State
//    private Integer covidSnapshotId;
//    private Integer locationIdFK;
//    private Integer activeCounty;
//    private Integer totalCounty;
//    private Integer activeState;
//    private Integer totalState;
//    private Integer activeCountry;
//    private Integer totalCountry;
//
//    // Location State
//    private Integer locationIdPK;
//    private String county;
//    private String state;
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        String propertyName = evt.getPropertyName();
//        switch (propertyName) {
//            case Constants.COVID_SNAPSHOT_ID:
//                this.setCovidSnapshotId((Integer) evt.getNewValue());
//                break;
//            case Constants.LOCATION_ID_FK:
//                this.setLocationIdFK((Integer) evt.getNewValue());
//                break;
//            case Constants.ACTIVE_COUNTY:
//                this.setActiveCounty((Integer) evt.getNewValue());
//                break;
//            case Constants.TOTAL_COUNTY:
//                this.setTotalCounty((Integer) evt.getNewValue());
//                break;
//            case Constants.ACTIVE_STATE:
//                this.setActiveState((Integer) evt.getNewValue());
//                break;
//            case Constants.TOTAL_STATE:
//                this.setTotalState((Integer) evt.getNewValue());
//                break;
//            case Constants.ACTIVE_COUNTRY:
//                this.setActiveCountry((Integer) evt.getNewValue());
//                break;
//            case Constants.TOTAL_COUNTRY:
//                this.setTotalCountry((Integer) evt.getNewValue());
//                break;
//            case Constants.LOCATION_ID_PK:
//                this.setLocationIdPK((Integer) evt.getNewValue());
//                break;
//            case Constants.COUNTY:
//                this.setCounty((String) evt.getNewValue());
//                break;
//            case Constants.STATE:
//                this.setState((String) evt.getNewValue());
//                break;
//        }
//    }
//
//    private void setCovidSnapshotId(Integer covidSnapshotId) { this.covidSnapshotId = covidSnapshotId; }
//
//    private void setLocationIdFK(Integer locationIdFK) { this.locationIdFK = locationIdFK; }
//
//    private void setActiveCounty(Integer activeCounty) { this.activeCounty = activeCounty; }
//
//    private void setTotalCounty(Integer totalCounty) { this.totalCounty = totalCounty; }
//
//    private void setActiveState(Integer activeState) { this.activeState = activeState; }
//
//    private void setTotalState(Integer totalState) { this.totalState = totalState; }
//
//    private void setActiveCountry(Integer activeCountry) { this.activeCountry = activeCountry; }
//
//    private void setTotalCountry(Integer totalCountry) { this.totalCountry = totalCountry; }
//
//    private void setLocationIdPK(Integer locationIdPK) { this.locationIdPK = locationIdPK; }
//
//    private void setCounty(String county) { this.county = county; }
//
//    private void setState(String state) { this.state = state; }
//
//}
