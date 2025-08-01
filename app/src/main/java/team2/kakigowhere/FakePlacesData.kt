package team2.kakigowhere

import team2.kakigowhere.data.model.Place

object FakePlacesData {
    private val PlacesList = listOf(
        Place(1,"Marina Bay Sands", "The Marina Bay Sands is the Marina Bay Sands","https://upload.wikimedia.org/wikipedia/commons/f/f9/Marina_Bay_Sands_in_the_evening_-_20101120.jpg", "https://www.marinabaysands.com/","Open 24 hours a day", "Open 24 hours a day",1.2838,103.8591,active = true),
        Place(2, "Singapore Zoo","The Singapore Zoo is one of the Zoos of all time","https://upload.wikimedia.org/wikipedia/commons/5/50/Singapore_Zoo_entrance-15Feb2010.jpg","https://www.mandai.com","8.30am","6.00pm", 1.4043,103.7930,active = true),
        Place(3,"Sentosa Island", "Sentosa Island is an island","https://upload.wikimedia.org/wikipedia/commons/9/97/1_sentosa_aerial_panorama_2016_from_south.jpg","www.sentosa.com.sg","Open 24 hours a day","Open 24 hours a day",1.2494,103.8303,true)
    )

    fun getPlaces(): List<Place> = PlacesList
}