# CapstoneProject - Daily Update 

[Udacity's Android Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) graduation project.

An app to help developers stay updated with hot repositories on GitHub or nearby events on Meetup related to their interests.

##  ** ON-GOING **

Please check [Capstone_Stage1.pdf](https://github.com/ootahiaoo/CapstoneProject/blob/master/Capstone_Stage1.pdf) for more details.


## How to install

### Step 1
Clone the repository using git (or download it as a zip), then import the project in Android Studio.
```
git clone https://github.com/ootahiaoo/CapstoneProject.git
```

### Step 2
The app fetches information from the [Guardian API](http://open-platform.theguardian
.com/documentation/) and uses GooglePlayServices.
You need to register for both and get your own API keys in order to use this app.
Once you have a key, open the `gradle.properties` file and replace the `your-api-key` fields
with your own keys.
```
meetupApiKey="your-api-key"
geoApiKey="your-api-key"
```


## License
_To be added._

Feel free to make pull requests/suggest improvements.