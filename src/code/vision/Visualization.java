package code.vision;

import code.core.aircraft.Aircraft;
import code.core.aircraft.State;
import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.io.IOException;

public class Visualization {
    public Visualization(File file,Aircraft aircraft) throws IOException {
        Kml kml=new Kml();
        Icon icon=new Icon().withHref(new File("").getAbsolutePath()+"\\src\\code\\vision\\plane.png");

        Document document=kml.createAndSetDocument().withOpen(true);
        Style style=document.createAndAddStyle().withId("HeHe");
        style.createAndSetLineStyle().withColor("FF0000FF");
        Folder plane=document.createAndAddFolder().withName("Plane's points");

        Folder tracks=document.createAndAddFolder().withName("Plane's lines");
        Placemark placemark2=tracks.createAndAddPlacemark().withStyleUrl("#HeHe");

        LineString lineString=placemark2.createAndSetLineString();
        lineString.withAltitudeMode(AltitudeMode.ABSOLUTE);
        lineString.withExtrude(true);
        lineString.withTessellate(true);

        for (int time:aircraft.realTracks.keySet()){
            if (time<aircraft.curFltPlan.startTime){
                continue;
            }
            State state =aircraft.realTracks.get(time);
            lineString.addToCoordinates(state.location.lng,state.location.lat,state.alt);

            Placemark placemark1=plane.createAndAddPlacemark().withDescription("Description:\n"+state);
            Style temp=placemark1.createAndAddStyle();
            temp.createAndSetIconStyle().withIcon(icon).withScale(0.5).withHeading(state.heading-90);

            placemark1.createAndSetPoint().withAltitudeMode(AltitudeMode.ABSOLUTE).addToCoordinates(state.location.lng,state.location.lat,state.alt);
            placemark1.createAndSetTimeStamp().withWhen(String.valueOf(time));
//            placemark1.createAndSetTimeSpan().withBegin(String.valueOf(time)).withEnd(String.valueOf(time));
        }
        kml.marshal(new File(file.getAbsolutePath()+"/"+aircraft.id+".kml"));
        System.out.println(aircraft.id+".kml has Saved successfully!");
    }
}
