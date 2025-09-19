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
 * Utility class for parsing KML (Keyhole Markup Language) files and extracting polygon geometries.
 * 
 * <p>This class provides static methods for parsing KML files that contain polygon data
 * and converting them to JTS (Java Topology Suite) Polygon objects for spatial operations.
 * The parser supports standard KML polygon structure with outer boundary definitions.</p>
 * 
 * <p>Supported KML structure:</p>
 * <pre>
 * &lt;Polygon&gt;
 *   &lt;outerBoundaryIs&gt;
 *     &lt;LinearRing&gt;
 *       &lt;coordinates&gt;lon,lat,alt lon,lat,alt ...&lt;/coordinates&gt;
 *     &lt;/LinearRing&gt;
 *   &lt;/outerBoundaryIs&gt;
 * &lt;/Polygon&gt;
 * </pre>
 * 
 * @author GIS Service Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class KmlParser {

    /** JTS GeometryFactory for creating polygon geometries */
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * Parses a KML file input stream and extracts the first polygon geometry found.
     * 
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Parses the KML XML structure</li>
     *   <li>Locates the first Polygon element</li>
     *   <li>Extracts coordinate data from outerBoundaryIs/LinearRing</li>
     *   <li>Converts coordinates to JTS Polygon object</li>
     *   <li>Ensures polygon closure (first and last coordinates match)</li>
     * </ul>
     *
     * @param kmlInputStream the input stream containing KML data
     * @return JTS Polygon object representing the parsed geometry
     * @throws Exception if KML parsing fails, no polygon found, or invalid coordinate data
     */
    public static Polygon parsePolygon(InputStream kmlInputStream) throws Exception {
        log.debug("Starting KML polygon parsing");
        
        try {
            // Configure XML parser for namespace-aware parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(kmlInputStream);
            
            log.debug("KML document parsed successfully");

            // Look for Polygon elements in the KML
            NodeList polygonNodes = document.getElementsByTagName("Polygon");
            if (polygonNodes.getLength() == 0) {
                log.error("No Polygon elements found in KML file");
                throw new RuntimeException("No Polygon found in KML file");
            }
            
            log.debug("Found {} polygon(s) in KML file, processing first one", polygonNodes.getLength());

            // Get the first polygon
            Element polygonElement = (Element) polygonNodes.item(0);
            
            // Look for outerBoundaryIs -> LinearRing -> coordinates
            NodeList outerBoundaryNodes = polygonElement.getElementsByTagName("outerBoundaryIs");
            if (outerBoundaryNodes.getLength() == 0) {
                log.error("No outer boundary found in Polygon element");
                throw new RuntimeException("No outer boundary found in Polygon");
            }

            Element outerBoundaryElement = (Element) outerBoundaryNodes.item(0);
            NodeList linearRingNodes = outerBoundaryElement.getElementsByTagName("LinearRing");
            if (linearRingNodes.getLength() == 0) {
                log.error("No LinearRing found in outer boundary");
                throw new RuntimeException("No LinearRing found in outer boundary");
            }

            Element linearRingElement = (Element) linearRingNodes.item(0);
            NodeList coordinatesNodes = linearRingElement.getElementsByTagName("coordinates");
            if (coordinatesNodes.getLength() == 0) {
                log.error("No coordinates found in LinearRing");
                throw new RuntimeException("No coordinates found in LinearRing");
            }

            String coordinatesText = coordinatesNodes.item(0).getTextContent().trim();
            log.debug("Extracted coordinates text: {}", coordinatesText.substring(0, Math.min(100, coordinatesText.length())));
            
            List<Coordinate> coordinates = parseCoordinates(coordinatesText);
            log.debug("Parsed {} coordinates from KML", coordinates.size());

            if (coordinates.size() < 4) {
                log.error("Insufficient coordinates: {}. Polygon must have at least 4 coordinates", coordinates.size());
                throw new RuntimeException("Polygon must have at least 4 coordinates");
            }

            // Ensure the polygon is closed (first and last coordinates are the same)
            if (!coordinates.get(0).equals(coordinates.get(coordinates.size() - 1))) {
                log.debug("Closing polygon by adding first coordinate as last");
                coordinates.add(new Coordinate(coordinates.get(0)));
            }

            Coordinate[] coordinateArray = coordinates.toArray(new Coordinate[0]);
            Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinateArray);
            log.info("Successfully created polygon with {} vertices", polygon.getCoordinates().length);
            
            return polygon;

        } catch (Exception e) {
            log.error("Failed to parse KML polygon", e);
            throw new RuntimeException("Failed to parse KML polygon: " + e.getMessage(), e);
        }
    }

    /**
     * Parses coordinate string from KML format into JTS Coordinate objects.
     * 
     * <p>KML coordinates are formatted as "longitude,latitude,altitude" separated by spaces.
     * This method handles the parsing and creates Coordinate objects with proper
     * longitude, latitude, and optional altitude values.</p>
     * 
     * <p>Example input: "-75.2,39.6,0 -74.6,39.6,0 -74.6,40.1,0"</p>
     *
     * @param coordinatesText the coordinate string from KML coordinates element
     * @return list of JTS Coordinate objects parsed from the input
     * @throws NumberFormatException if coordinate values cannot be parsed as doubles
     */
    private static List<Coordinate> parseCoordinates(String coordinatesText) {
        List<Coordinate> coordinates = new ArrayList<>();
        String[] coordStrings = coordinatesText.split("\\s+");
        
        log.debug("Parsing {} coordinate strings", coordStrings.length);

        for (String coordString : coordStrings) {
            coordString = coordString.trim();
            if (!coordString.isEmpty()) {
                String[] parts = coordString.split(",");
                if (parts.length >= 2) {
                    try {
                        double lon = Double.parseDouble(parts[0]);
                        double lat = Double.parseDouble(parts[1]);
                        double alt = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                        coordinates.add(new Coordinate(lon, lat, alt));
                        log.trace("Parsed coordinate: lon={}, lat={}, alt={}", lon, lat, alt);
                    } catch (NumberFormatException e) {
                        log.warn("Skipping invalid coordinate: {}", coordString);
                    }
                } else {
                    log.warn("Skipping malformed coordinate (insufficient parts): {}", coordString);
                }
            }
        }
        
        log.debug("Successfully parsed {} valid coordinates", coordinates.size());
        return coordinates;
    }
}
