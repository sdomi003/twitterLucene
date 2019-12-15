# TwitterViewer
Twitter Viewer is a webapp that lets you view local tweets on a map. 

![Example of the UI](https://github.com/CS-UCR/final-project-carriedbyjoker/blob/master/twitterviewer.png)

# Features

* Search for tweets!
* See the location of the tweet's origins on a map!
* Click on a map pin to see the tweet from that location!
  


# Implementation
* Web server is APACHE TomCat.
* Built using Intellij Ultimate Edition thanks to free licence for students.
* Tweets collected using Tweepy.
* Indexing and searching is done using APACHE Lucene.
* Maps is done thorugh Google Maps API
  

# How To Run It
Dependencies - Make Sure You Have:
* Apache Tomcat v9
* Apache Lucene v8
* Intellij Ultime (not Community version), Eclipse, or knowledge of setting up Tomcat server.
  * Intellij Ultimate is free with .edu email 
# Directions
* Clone Repo
* link project to tomcat server
  * If using Intellij Ultimate, specify the path to your tomcat directory.
* Run and open browser to whatever port tomcat is using
* type in a user query, hit enter, and click on pins to see tweets
