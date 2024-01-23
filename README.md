This project makes use of pedestrian foot traffic data collected bi-yearly from 2007 to 2021 at 141 locations across New York City. For each bi-yearly count foot traffic was tracked at each location on a weekday (in the morning and evening, counted separately) and at mid-day on a weekend day. This project uses the Unfolding library which is part of the Processing visual output library to display a map for the user. When the user presses the '1' through '6' keys they can see six different visualizations of the data, with bubble markers at each of the locations where data was collected. The size of the marker corresponds to the amount of foot traffic at that location on the given day. 

When the '1' key is pressed the map shows data collected on a weekday morning in May 2021.

When the '2' key is pressed the map shows data collected on a weekday evening in May 2021.

When the '3' key is pressed the map shows the difference between the morning and evening foot traffic on a weekday in May 2021.

When the '4' key is pressed the map shows the difference between the averages of foot traffic on a weekday in May 2021 and a weekday in May 2019.

When the '5' key is pressed the map shows data collected on a weekend day in October 2020.

When the '6' key is pressed the map shows data collected on a weekend day in May 2021.

For visualizations showing the difference between two counts, see the documentation of the keyPressed() method in App.java to see what the colors of bubbles mean.

NOTE: Processing only works with Java 1.8 so you must set your JDK accordingly before running this project. 