package code.vision;

import code.core.simulation.Routing;
import code.core.simulation.WayPoint;
import code.util.DataAccess;
import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Basics {
    final Kml kml=new Kml();
    public Basics() throws FileNotFoundException {
        Document document=kml.createAndSetDocument().withName("Basical elements").withOpen(true);
        Style common=document.createAndAddStyle().withId("sector_color");
        common.createAndSetLineStyle().withColor("7f00ffff").withWidth(2);

        /**points**/
        Folder points_folder=document.createAndAddFolder().withName("Points").withOpen(false);
        Icon icon=new Icon().withHref(new File("").getAbsolutePath()+"\\src\\code\\vision\\point.png");

        Map<String,WayPoint> wayPointMap=DataAccess.allNaipPtMap;
        for (String id: wayPointMap.keySet()){
            WayPoint point=wayPointMap.get(id);
            Placemark placemark=points_folder.createAndAddPlacemark().withName(point.id);
            placemark.createAndSetPoint().addToCoordinates(point.lng,point.lat);

            Style style=placemark.createAndAddStyle();
            style.createAndSetIconStyle().withScale(0.5).withIcon(icon);
        }

        /**routes**/
        Folder routes_folder=document.createAndAddFolder().withName("Routes").withOpen(false);
        Map<String, Routing> routingMap=DataAccess.allRoutingMap;

        for (String route_id:routingMap.keySet()){
            Routing routing=routingMap.get(route_id);

            LineString line=routes_folder.createAndAddPlacemark().withName(route_id).createAndSetLineString();
            line.setAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
            line.setExtrude(true);
            line.setTessellate(true);

            for (WayPoint wayPoint:routing.getPoints()){
                line.addToCoordinates(wayPoint.lng,wayPoint.lat,wayPoint.alt);
            }
        }

        /**sectors**/
        Folder sectors_folder=document.createAndAddFolder().withName("sectors").withOpen(false);
        Map<List<String>,List<WayPoint>> sectorMap=DataAccess.sectorMap;

        String tempLinename;

        for (List<String> strings:sectorMap.keySet()){
            List<WayPoint> temp=sectorMap.get(strings);

            String artcc=strings.get(0);
            String area=strings.get(1);
            String name=strings.get(2);

            tempLinename=area!=null && area.length()>=1?(name!=null && name.length()>=1?name:area):artcc;

            LineString lineString=sectors_folder.createAndAddPlacemark().withName(tempLinename).withStyleUrl(common.getId()).createAndSetLineString();
            lineString.setAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
            lineString.setExtrude(true);
            lineString.setTessellate(true);

            for(WayPoint wayPoint:temp){
                lineString.addToCoordinates(wayPoint.lng,wayPoint.lat,wayPoint.alt);
            }
            WayPoint wayPoint=temp.get(0);
            lineString.addToCoordinates(wayPoint.lng,wayPoint.lat,wayPoint.alt);
        }

        kml.marshal(new File("src/code/vision/Basics.kml"));
        System.out.println("Basics.kml has Saved successfully!");
    }
}
