
// some basic java imports
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.ArrayUtils;


// some imports used by the UnfoldingMap library
import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.*;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.providers.Google.*;
import de.fhpotsdam.unfolding.providers.Microsoft;
// import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * A program that opens an interactive map of pedestrian counts data at various
 * location in NYC.
 */
public class App extends PApplet {

	UnfoldingMap map; // reference to the actual map
	String mapTitle; // title of the map
	final float SCALE_FACTOR = 0.0002f; // a factor used to scale pedestrian counts to calculate a reasonable radius for
										// a bubble marker on the map
	final int DEFAULT_ZOOM_LEVEL = 11;
	final Location DEFAULT_LOCATION = new Location(40.7286683f, -73.997895f); // a hard-coded NYC location to start with
	String[][] data; // will hold data extracted from the CSV data file

	/**
	 * This method is automatically called every time the user presses a key while
	 * viewing the map.
	 * The `key` variable is automatically assigned the value of the
	 * key that was pressed.
	 * - when the user presses the `1` key, the code calls the
	 * showMay2021MorningCounts method to show the morning counts in May 2021, with
	 * blue bubble markers on the map.
	 * - when the user presses the `2` key, the code calls the
	 * showMay2021EveningCounts method to show the evening counts in May 2021, with
	 * blue bubble markers on the map.
	 * - when the user presses the `3` key, the code calls the
	 * showMay2021EveningMorningCountsDifferencemethod to show the difference
	 * between the evening and morning counts in May 2021. If the evening count is
	 * greater, the marker should be a green bubble, otherwise, the marker should be
	 * a red bubble.
	 * - when the user presses the `4` key, the code calls the
	 * showMay2021VersusMay2019Counts method to show the difference between the
	 * average of the evening and morning counts in May 2021 and the average of the
	 * evening and morning counts in May 2019. If the counts for 2021 are greater,
	 * the marker should be a green bubble, otherwise, the marker should be a red
	 * bubble.
	 * - when the user presses the `5` key, the code calls the customVisualization1
	 * method to show data of your choosing, visualized with marker types of your
	 * choosing.
	 * - when the user presses the `6` key, the code calls the customVisualization2
	 * method to show data of your choosing, visualized with marker types of your
	 * choosing.
	 */
	public void keyPressed() {
		switch (key) {
			case '1':
				showMay2021MorningCounts(data);
				break;
			case '2':
				showMay2021EveningCounts(data);
				break;
			case '3':
				showMay2021EveningMorningCountsDifference(data);
				break;
			case '4':
				showMay2021VersusMay2019Counts(data);
				break;
			case '5':
				showOcotober2020WeekendCounts(data);
				break;
			case '6':
				showMay2021WeekendCounts(data);
				break;
		}
		System.out.println("Key pressed: " + key);
	}

	/**
	 * Parses String for an Integer, returns 0 if no Integer found. 
	 * 
	 * @param count String to be parsed
	 * @return Integer found in String or 0
	 */
	public int pedCount(String count) {
		if (count != null && !count.trim().isEmpty()) {
			return Integer.parseInt(count);
		} else {
			return 0;
		}
	}

	/**
	 * Chooses color of marker bubble in cases where differences in data are being shown. 
	 * 
	 * @param count1 first count at a given location
	 * @param count2 second count at that location
	 * @return correct bubble color as RGB value
	 */
	public float[] colorDecider(int count1, int count2) {
		if (count1 > count2) {
			return new float[] { 0, 255, 0, 127 }; // green
		} else {
			return new float[] { 255, 0, 0, 127 }; // red
		}
	}

	/**
	 * Modularizes process of displaying foot traffic data from a specific instance.
	 * Clears map of existing data, reads foot traffic data from each location where information was recorded, ignoring null values
	 * and adds a marker to the map for each location. 
	 * 
	 * @param data String[][] where foot traffic data is stored
	 * @param title title of map
	 * @param offset distance of relevant column of information from end of file
	 * @param bubbleColor color of bubble to displayed
	 */
	public void showCounts(String[][] data, String title, int offset, float[] bubbleColor) {
		clearMap();
		mapTitle = title;
		for (String[] day : data) {
			if (day[day.length - offset].trim().isEmpty()) {
				continue; // skipping over empty values
			}
			float lng = Float.parseFloat(day[0]);
			float lat = Float.parseFloat(day[1]);
			Location markerLocation = new Location(lat, lng);
			int pedestrianCount = pedCount(day[day.length - offset]);
			float markerRadius = pedestrianCount * SCALE_FACTOR;
			float[] markerColor = bubbleColor;
			MarkerBubble marker = new MarkerBubble(this, markerLocation, markerRadius, markerColor);
			map.addMarker(marker);
		}
	}

	/**
	 * Adds markers to the map for the morning pedestrian counts in May 2021.
	 * These counts are in the second-to-last field in the CSV data file. So we look
	 * at the second-to-last array element in our data array for these values.
	 * 
	 * @param data A two-dimensional String array, containing the data returned by
	 *             the getDataFromLines method.
	 */
	public void showMay2021MorningCounts(String[][] data) {
		showCounts(data, "May 2021 Morning Pedestrian Counts", 3, new float[] { 0, 0, 255, 127 });
	}

	/**
	 * Adds markers to the map for the evening pedestrian counts in May 2021.
	 * These counts are in the second-to-last field in the CSV data file. So we look
	 * at the second-to-last array element in our data array for these values.
	 * 
	 * @param data A two-dimensional String array, containing the data returned by
	 *             the getDataFromLines method.
	 */
	public void showMay2021EveningCounts(String[][] data) {
		showCounts(data, "May 2021 Evening Pedestrian Counts", 2, new float[] { 0, 0, 255, 127 });
	}

	/**
	 * Adds markers to the map for the difference between evening and morning
	 * pedestrian counts in May 2021.
	 * 
	 * @param data A two-dimensional String array, containing the data returned by
	 *             the getDataFromLines method.
	 */
	public void showMay2021EveningMorningCountsDifference(String[][] data) {
		clearMap(); // clear any markers previously placed on the map
		mapTitle = "Difference Between May 2021 Evening and Morning Pedestrian Counts";
		for (String[] day : data) {
			if (day[day.length - 3].trim().isEmpty() || day[day.length - 2].trim().isEmpty()) {
				continue;
			}
			float lng = Float.parseFloat(day[0]);
			float lat = Float.parseFloat(day[1]);
			Location markerLocation = new Location(lat, lng);
			int eveningCount = pedCount(day[day.length - 2]);
			int morningCount = pedCount(day[day.length - 3]);
			int pedestrianCount = abs(eveningCount - morningCount);
			float markerRadius = pedestrianCount * SCALE_FACTOR;
			float[] markerColor = colorDecider(eveningCount, morningCount);
			MarkerBubble marker = new MarkerBubble(this, markerLocation, markerRadius, markerColor);
			map.addMarker(marker);
		}
	}

	/**
	 * Adds markers to the map for the difference between the average pedestrian
	 * count in May 2021 and the average pedestrian count in May 2019.
	 * 
	 * @param data A two-dimensional String array, containing the data returned by
	 *             the getDataFromLines method.
	 */
	public void showMay2021VersusMay2019Counts(String[][] data) {
		clearMap(); // clear any markers previously placed on the map
		mapTitle = "Difference Between May 2021 and May 2019 Pedestrian Counts";
		for (String[] day : data) {
			if (day[day.length - 7].trim().isEmpty() || day[day.length - 8].trim().isEmpty()
					|| day[day.length - 9].trim().isEmpty()) {
				continue;
			}
			float lng = Float.parseFloat(day[0]);
			float lat = Float.parseFloat(day[1]);
			Location markerLocation = new Location(lat, lng);
			int count2019 = (pedCount(day[day.length - 7]) + pedCount(day[day.length - 8])
					+ pedCount(day[day.length - 9])) / 3;
			int count2021 = (pedCount(day[day.length - 1]) + pedCount(day[day.length - 2])
					+ pedCount(day[day.length - 3])) / 3;
			int pedestrianCount = abs(count2019 - count2021);
			float markerRadius = pedestrianCount * SCALE_FACTOR;
			float[] markerColor = colorDecider(count2021, count2019);
			MarkerBubble marker = new MarkerBubble(this, markerLocation, markerRadius, markerColor);
			map.addMarker(marker);
		}
	}

	/**
	 * Shows October 2020 Weekend pedestrian counts
	 * 
	 * @param data
	 */
	public void showOcotober2020WeekendCounts(String[][] data) {
		showCounts(data, "October 2020 Weekend Pedestrian Counts", 4, new float[] { 0, 255, 239, 127 });
	}

	/**
	 * Shows May 2021 Weekend Pedestrian Counts
	 * 
	 * @param data
	 */
	public void showMay2021WeekendCounts(String[][] data) {
		showCounts(data, "May 2021 Weekend Pedestrian Counts", 1, new float[] { 2, 162, 0, 127 });
	}

	/**
	 * Opens a file and returns an array of the lines within the file, as Strings
	 * with their line breaks removed.
	 * 
	 * @param filepath The filepath to open
	 * @return A String array, where each String contains the text of a line of the
	 *         file, with its line break removed.
	 * @throws FileNotFoundException
	 */
	public String[] getLinesFromFile(String filepath) {
		ArrayList<String> fullText = new ArrayList<String>();
		try {
			Scanner scn = new Scanner(new File(filepath));
			if (scn.hasNextLine()) {
				scn.nextLine(); // discarding header
			}
			while (scn.hasNextLine()) {
				fullText.add(scn.nextLine().trim());
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println("Oh no... can't find the file!");
		}
		return fullText.toArray(new String[0]); // converting ArrayList to String[]
	}

	// pulls coordinates of location from first column of line in csv
	public String[] parsePoint(String location) { 
		String[] geom = location.split(" ");
		String lng = geom[1].substring(1);
		String lat = geom[2].substring(0, geom[2].length() - 1);
		String[] coord = { lng, lat };
		return coord;
	}

	/**
	 * Takes an array of lines of text in comma-separated values (CSV) format and
	 * splits each line into a sub-array of data fields.
	 *
	 * @param lines A String array of lines of text, where each line is in
	 *              comma-separated values (CSV) format.
	 * @return A two-dimensional String array, where each inner array contains the
	 *         data from one of the lines, split by commas.
	 */
	public String[][] getDataFromLines(String[] lines) {
		String[][] allLines = new String[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			String[] splitData = lines[i].split(",");
			String[] coordinates = parsePoint(splitData[0]);

			// concatenates coordinates to location array
			allLines[i] = ArrayUtils.addAll(coordinates, Arrays.copyOfRange(splitData, 1, splitData.length));
		}
		return allLines;
	}

	/**
	 * Initial setup of the window, the map, and markers.
	 */
	public void setup() {
		size(1200, 800, P2D); // set the map window size, using the OpenGL 2D rendering engine
		map = getMap(); // create the map and store it in the global-ish map variable
		try {
			String cwd = Paths.get("").toAbsolutePath().toString();
			String path = Paths.get(cwd, "data", "PedCountData.csv").toString();
			String[] lines = getLinesFromFile(path);
			data = getDataFromLines(lines);

			// automatically zoom and pan into the New York City location
			map.zoomAndPanTo(DEFAULT_ZOOM_LEVEL, DEFAULT_LOCATION);

			// by default, show markers for the morning counts in May 2021 (the
			// third-to-last field in the CSV file)
			showMay2021MorningCounts(data);
		} catch (Exception e) {
			System.out.println("Error: could not load data from file: " + e);
		}

	} // setup

	/**
	 * Create a map using a publicly-available map provider.
	 * If there are error messages related to the Map Provider or with loading the
	 * map tile image files, try all of the other commented-out map providers to see
	 * if one works.
	 * 
	 * @return A map object.
	 */
	private UnfoldingMap getMap() {
		// not all map providers work on all computers.
		// if you have trouble with the one selected, try the others one-by-one to see
		// which one works for you.
		map = new UnfoldingMap(this, new Microsoft.RoadProvider());
		// map = new UnfoldingMap(this, new Microsoft.AerialProvider());
		// map = new UnfoldingMap(this, new GoogleMapProvider());
		// map = new UnfoldingMap(this);
		// map = new UnfoldingMap(this, new OpenStreetMapProvider());

		// enable some interactive behaviors
		MapUtils.createDefaultEventDispatcher(this, map);
		map.setTweening(true);
		map.zoomToLevel(DEFAULT_ZOOM_LEVEL);

		return map;
	}

	/**
	 * This method automatically draws the map.
	 */
	public void draw() {
		background(0);
		map.draw();
		drawTitle();
	}

	/**
	 * Clear the map of all markers.
	 */
	public void clearMap() {
		map.getMarkers().clear();
	}

	/**
	 * Draw the title of the map at the bottom of the screen
	 */
	public void drawTitle() {
		fill(0);
		noStroke();
		rect(0, height - 40, width, height - 40); // black rectangle
		textAlign(CENTER);
		fill(255);
		text(mapTitle, width / 2, height - 15); // white centered text
	}

	/**
	 * Checks for correct JDK and runs program.
	 * 
	 * @param args A String array of command-line arguments.
	 */
	public static void main(String[] args) {
		System.out.printf("\n###  JDK IN USE ###\n- Version: %s\n- Location: %s\n### ^JDK IN USE ###\n\n",
				SystemUtils.JAVA_VERSION, SystemUtils.getJavaHome());
		boolean isGoodJDK = SystemUtils.IS_JAVA_1_8;
		if (!isGoodJDK) {
			System.out.printf("Fatal Error: YOU MUST USE JAVA 1.8, not %s!!!\n", SystemUtils.JAVA_VERSION);
		} else {
			PApplet.main("App");
		}
	}

}
