# WeatherXMLParser
Catches data from included weather station simulator.
This is edited, sorted and then written to a predetermined location.
The data is deleted if it is 3+ months old.
Works on maximum settings, but may need to scale up at ~200 station clusters at a time to prevent a crash.

Usage: run the os-appropriate scripts of both programs (parser one in out/artifacts>).

xml data will be in new directory;
{this}/parsedweatherdata for windows
/home/pi/mnt/weatherdata/ for linux



