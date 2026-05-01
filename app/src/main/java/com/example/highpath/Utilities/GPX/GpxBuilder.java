package com.example.highpath.Utilities.GPX;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GpxBuilder {

    private final String filePath;
    private final String fileName;

    public GpxBuilder(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    private void fileCreator() throws IOException {
        File gpxFile = new File(filePath, fileName);
        if (!gpxFile.exists()) {
            gpxFile.createNewFile();
        }
    }

    public void gpxHeader() throws Exception {
        fileCreator();
        File gpxFile = new File(filePath, fileName);
        FileWriter writer = new FileWriter(gpxFile, true);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.append("<gpx version=\"1.1\" creator=\"HighPath\"\n");
        writer.append("     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        writer.append("     xmlns:ogr=\"http://osgeo.org/gdal\"\n");
        writer.append("     xmlns=\"http://www.topografix.com/GPX/1/1\"\n");
        writer.append("     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 ");
        writer.append("http://www.topografix.com/GPX/1/1/gpx.xsd\">\n");
        writer.append("  <trk>\n");
        writer.append("    <name>").append(fileName).append("</name>\n");
        writer.append("    <trkseg>\n");

        writer.close();
    }

    @SuppressLint("DefaultLocale")
    public void trackPoint(double lat, double lon, double ele) throws IOException {
        File gpxFile = new File(filePath, fileName);
        FileWriter writer = new FileWriter(gpxFile, true);

        String time = java.time.Instant.now().toString();

        writer.write(String.format("\n      <trkpt lat=\"%f\" lon=\"%f\">\n", lat, lon));
        writer.write(String.format("        <ele>%f</ele>\n", ele));
        writer.write(String.format("        <time>%s</time>\n", time));
        writer.write("      </trkpt>");

        writer.close();
    }

    public String gpxFooter() throws IOException {
        File gpxFile = new File(filePath, fileName);
        FileWriter writer = new FileWriter(gpxFile, true);

        writer.write("\n    </trkseg>");
        writer.write("\n  </trk>");
        writer.write("\n</gpx>");

        writer.close();
        return gpxFile.getAbsolutePath();
    }
}