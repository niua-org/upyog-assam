package org.upyog.gis.util;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing KML files and extracting polygon coordinates
 */
@Slf4j
public class KmlParser {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * Parse KML file and extract the first polygon found
     *
     * @param kmlInputStream the KML file input stream
     * @return JTS Polygon object
     * @throws Exception if parsing fails
     */
    public static Polygon parsePolygon(InputStream kmlInputStream) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(kmlInputStream);

            // Look for Polygon elements in the KML
            NodeList polygonNodes = document.getElementsByTagName("Polygon");
            if (polygonNodes.getLength() == 0) {
                throw new RuntimeException("No Polygon found in KML file");
            }

            // Get the first polygon
            Element polygonElement = (Element) polygonNodes.item(0);
            
            // Look for outerBoundaryIs -> LinearRing -> coordinates
            NodeList outerBoundaryNodes = polygonElement.getElementsByTagName("outerBoundaryIs");
            if (outerBoundaryNodes.getLength() == 0) {
                throw new RuntimeException("No outer boundary found in Polygon");
            }

            Element outerBoundaryElement = (Element) outerBoundaryNodes.item(0);
            NodeList linearRingNodes = outerBoundaryElement.getElementsByTagName("LinearRing");
            if (linearRingNodes.getLength() == 0) {
                throw new RuntimeException("No LinearRing found in outer boundary");
            }

            Element linearRingElement = (Element) linearRingNodes.item(0);
            NodeList coordinatesNodes = linearRingElement.getElementsByTagName("coordinates");
            if (coordinatesNodes.getLength() == 0) {
                throw new RuntimeException("No coordinates found in LinearRing");
            }

            String coordinatesText = coordinatesNodes.item(0).getTextContent().trim();
            List<Coordinate> coordinates = parseCoordinates(coordinatesText);

            if (coordinates.size() < 4) {
                throw new RuntimeException("Polygon must have at least 4 coordinates");
            }

            // Ensure the polygon is closed (first and last coordinates are the same)
            if (!coordinates.get(0).equals(coordinates.get(coordinates.size() - 1))) {
                coordinates.add(new Coordinate(coordinates.get(0)));
            }

            Coordinate[] coordinateArray = coordinates.toArray(new Coordinate[0]);
            return GEOMETRY_FACTORY.createPolygon(coordinateArray);

        } catch (Exception e) {
            log.error("Failed to parse KML polygon", e);
            throw new RuntimeException("Failed to parse KML polygon: " + e.getMessage(), e);
        }
    }

    /**
     * Parse coordinate string from KML format (lon,lat,alt separated by spaces)
     *
     * @param coordinatesText the coordinate string
     * @return list of coordinates
     */
    private static List<Coordinate> parseCoordinates(String coordinatesText) {
        List<Coordinate> coordinates = new ArrayList<>();
        String[] coordStrings = coordinatesText.split("\\s+");

        for (String coordString : coordStrings) {
            coordString = coordString.trim();
            if (!coordString.isEmpty()) {
                String[] parts = coordString.split(",");
                if (parts.length >= 2) {
                    double lon = Double.parseDouble(parts[0]);
                    double lat = Double.parseDouble(parts[1]);
                    double alt = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                    coordinates.add(new Coordinate(lon, lat, alt));
                }
            }
        }

        return coordinates;
    }
}
