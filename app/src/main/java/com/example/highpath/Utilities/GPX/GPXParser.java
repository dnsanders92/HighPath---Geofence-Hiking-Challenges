package com.example.highpath.Utilities.GPX;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GPXParser {

    public GPXData parse(String filePath) throws Exception {

        File gpxFile = new File(filePath);

        Document doc = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(gpxFile);

        doc.getDocumentElement().normalize();

        NodeList trackPoints = doc.getElementsByTagName("trkpt");

        GPXData data = new GPXData();
        data.latitudes = new ArrayList<>();
        data.longitudes = new ArrayList<>();
        data.elevations = new ArrayList<>();
        data.timestamps = new ArrayList<>();

        for (int i = 0; i < trackPoints.getLength(); i++) {

            Element trkpt = (Element) trackPoints.item(i);

            double latitude = Double.parseDouble(trkpt.getAttribute("lat"));
            double longitude = Double.parseDouble(trkpt.getAttribute("lon"));

            data.latitudes.add(latitude);
            data.longitudes.add(longitude);

            NodeList ele = trkpt.getElementsByTagName("ele");
            double elevation = 0.0;

            if (ele.getLength() > 0) {
                elevation = Double.parseDouble(ele.item(0).getTextContent());
            }

            data.elevations.add(elevation);

            NodeList timeList = trkpt.getElementsByTagName("time");

            if (timeList.getLength() > 0) {
                data.timestamps.add(timeList.item(0).getTextContent().trim());
            } else {
                data.timestamps.add(null);
            }
        }

        return data;
    }
}
