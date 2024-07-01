package araya.gonzalo.reproductorayudantia

data class Song(
    val title: String,
    val audioResId: Int,
    val imageResId: Int,
){}

class AppConstants {
    companion object{
        const val LOG_MAIN_ACTIVITY = "MainActivityReproductor"
        const val MEDIA_PLAYER_POSITION = "mpPosition"
        const val CURRENT_SONG_INDEX = ""
        val songs = listOf(
            Song("Pretty Please Remix - Dua Lipa", R.raw.pp_remix, R.drawable.pretty_please),
            Song("I lose control - Teddy Swims", R.raw.teddy_swims, R.drawable.teddy),
            Song("Here comes the rain - Eurythmics", R.raw.eurythmics, R.drawable.eurythmics),
            Song("Colors - Black Pumas", R.raw.black_pumas, R.drawable.black_pumas)
        )

    }

}