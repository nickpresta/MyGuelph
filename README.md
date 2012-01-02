# My Guelph

A student-made Android application for the University of Guelph.

Tested on:

* Android 4.0.1 (physical Galaxy Nexus)
* Android 2.3.6 (physical Nexus S)

Supports Android 2.3.3+.

## Planned Features
* Map. Building searching by name/course codes. Shows you and the target building. Useful for new students.
* News. Aggregates all the school feeds (Current News, At Guelph, Gryphons)
* Events. Pulls data from Student Affairs. Lets you register for events.
* Links. Important links for students and staff - Web Advisor, Courselink, GryphMail, CCS, Software Downloads, etc.

## Required Permissions
* **INTERNET:** To access the Internet - fetch News, Events, etc.
* **ACCESS_NETWORK_STATE:** Determine if you're connected to the Internet - prompts you to enable connection in Settings if you're not.
* **ACCESS_FINE_LOCATION:** Determine if GPS is enabled - prompts you to enable GPS in Settings if it isn't. Also used to track location for Map section. 

## Thanks
* Android developer docs
* \#android-dev on Freenode
* StackOverflow
* [Android-RSS-Reader-Library](https://github.com/matshofman/Android-RSS-Reader-Library) - Used for pulling down RSS data.
* [JSoup](http://jsoup.org/) - Used for parsing and fetching HTML pages.
* [Oxygen Icons](http://www.oxygen-icons.org/) - Icons used in application.
