# CapstoneProject - Daily Update 

[Udacity's Android Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) graduation project.

An app to help developers stay updated with hot repositories on GitHub or nearby events on Meetup related to their interests.
The user can set up a search and receive notifications when there is any new Meetup event matching the search criteria.

Please check [Capstone_Stage1.pdf](https://github.com/ootahiaoo/CapstoneProject/blob/master/Capstone_Stage1.pdf) for more details.

<p align="center">Mobile version</p>
<p align="center">
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_1.png" width="150" title="Home">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_2.png" width="150" title="Event search dialog">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_3.png" width="150" title="Meetup event search result">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_7.png" width="150" title="Meetup event details">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_4.png" width="150" title="GitHub repository search result">
</p>

<p align="center">
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_6.png" width="150" title="Bookmarks">
 
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_8.png" width="150" title="Settings">

<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_9.png" width="150" title="Widget">

<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_1-2.png" width="300" title="Home - landscape version">
</p>


<p align="center">Tablet version</p>
<p align="center">
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_10.png" width="200" title="Home - tablet version">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_11.png" width="200" title="Meetup event search result - tablet version">
  
<img src="https://raw.githubusercontent.com/ootahiaoo/CapstoneProject/master/screenshot/Screenshot_12-2.png" width="350" title="Meetup event details - tablet lanscape version">
</p>


### Optional future tasks:
- Separate the code to retrieve location from UI activities.
- Implement Dagger2/RxJava.
- Create the "Share" option to share the currently viewed repository or event.
- Add Connpass search option.
- Change the home UI to use Paging, display the results from GitHub, Meetup and Connpass APIs in different tabs.


## How to install

### Step 1
Clone the repository using git (or download it as a zip), then import the project in Android Studio.
```
git clone https://github.com/ootahiaoo/CapstoneProject.git
```

### Step 2
The app fetches information from the [Meetup API](https://www.meetup.com/meetup_api/), and retrieves
 the user's location with GooglePlayServices .
You need to register for both and get your own API keys in order to use this app.
Once you have them, open the `gradle.properties` file and replace the `your-api-key` fields
with your own keys.
```
meetupApiKey="your-api-key"
geoApiKey="your-api-key"
```


## License
_To be added._

Feel free to make pull requests/suggest improvements.
