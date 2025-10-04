package com.example.spotifyclone.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.entities.Cancion
import com.google.firebase.firestore.FirebaseFirestore

//
class AlbumViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var canciones = mutableStateOf<List<Cancion>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun cargarCanciones(albumId: String) { // 👈 también renombrado
        isLoading.value = true

        db.collection("album") // 👈 cambiado a "album"
            .document(albumId)
            .collection("canciones")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val cancion = doc.toObject(Cancion::class.java)
                    cancion?.copy(id = doc.id)
                }
                canciones.value = lista
                isLoading.value = false
            }
            .addOnFailureListener {
                canciones.value = emptyList()
                isLoading.value = false
            }
    }

    fun seedData() {
        val album = mapOf( // 👈 renombrado aquí
            "top_peru" to listOf(
                "La Colegiala" to "Rodrigo",
                "Hawái" to "Maluma",
                "Te Boté" to "Nio García",
                "Agua" to "Jarabe de Palo",
                "Una Vaina Loca" to "Fuego",
                "Llamado de Emergencia" to "Daddy Yankee",
                "Provenza" to "Karol G",
                "Felices los 4" to "Maluma",
                "Dákiti" to "Bad Bunny",
                "Pepas" to "Farruko",
                "Levitating" to "Dua Lipa",
                "Blinding Lights" to "The Weeknd",
                "Shape of You" to "Ed Sheeran",
                "La Modelo" to "Ozuna",
                "Corazón" to "Maluma ft. Nego do Borel",
                "Nota de Amor" to "Wisin & Carlos Vives",
                "Despacito" to "Luis Fonsi",
                "Tusa" to "Karol G",
                "Yonaguni" to "Bad Bunny",
                "Chantaje" to "Shakira ft. Maluma"
            ),
            "descubrimiento_semanal" to listOf(
                "Flowers" to "Miley Cyrus",
                "Unholy" to "Sam Smith ft. Kim Petras",
                "As It Was" to "Harry Styles",
                "Kill Bill" to "SZA",
                "Calm Down" to "Rema",
                "Ghost" to "Justin Bieber",
                "Golden Hour" to "JVKE",
                "Anti-Hero" to "Taylor Swift",
                "Stay" to "The Kid LAROI, Justin Bieber",
                "Montero" to "Lil Nas X",
                "Peaches" to "Justin Bieber",
                "Shivers" to "Ed Sheeran",
                "Industry Baby" to "Lil Nas X ft. Jack Harlow",
                "abcdefu" to "GAYLE",
                "Good 4 U" to "Olivia Rodrigo",
                "Levitating" to "Dua Lipa",
                "Save Your Tears" to "The Weeknd",
                "Don’t Start Now" to "Dua Lipa",
                "Break My Heart" to "Dua Lipa",
                "Heat Waves" to "Glass Animals"
            ),
            "exitos_globales" to listOf(
                "Blinding Lights" to "The Weeknd",
                "Shape of You" to "Ed Sheeran",
                "Uptown Funk" to "Bruno Mars",
                "Rolling in the Deep" to "Adele",
                "Thinking Out Loud" to "Ed Sheeran",
                "Happy" to "Pharrell Williams",
                "Someone Like You" to "Adele",
                "Señorita" to "Shawn Mendes ft. Camila Cabello",
                "Closer" to "The Chainsmokers ft. Halsey",
                "See You Again" to "Wiz Khalifa ft. Charlie Puth",
                "Rockstar" to "Post Malone",
                "Sunflower" to "Post Malone, Swae Lee",
                "Perfect" to "Ed Sheeran",
                "Sorry" to "Justin Bieber",
                "Bad Guy" to "Billie Eilish",
                "Shake It Off" to "Taylor Swift",
                "Can’t Stop the Feeling" to "Justin Timberlake",
                "Old Town Road" to "Lil Nas X",
                "Sugar" to "Maroon 5",
                "Believer" to "Imagine Dragons"
            ),
            "rock" to listOf(
                "Bohemian Rhapsody" to "Queen",
                "Stairway to Heaven" to "Led Zeppelin",
                "Hotel California" to "Eagles",
                "Sweet Child O’ Mine" to "Guns N’ Roses",
                "Smells Like Teen Spirit" to "Nirvana",
                "Nothing Else Matters" to "Metallica",
                "Back In Black" to "AC/DC",
                "Paint It Black" to "The Rolling Stones",
                "Wish You Were Here" to "Pink Floyd",
                "Highway to Hell" to "AC/DC",
                "November Rain" to "Guns N’ Roses",
                "Enter Sandman" to "Metallica",
                "Another Brick in the Wall" to "Pink Floyd",
                "Paranoid" to "Black Sabbath",
                "Livin’ on a Prayer" to "Bon Jovi",
                "Don’t Stop Believin’" to "Journey",
                "Kashmir" to "Led Zeppelin",
                "Whole Lotta Love" to "Led Zeppelin",
                "We Will Rock You" to "Queen",
                "Comfortably Numb" to "Pink Floyd"
            ),
            "novedades_latinas" to listOf(
                "QLONA" to "Karol G ft. Peso Pluma",
                "BZRP Music Sessions #55" to "Bizarrap ft. Peso Pluma",
                "Me Porto Bonito" to "Bad Bunny ft. Chencho Corleone",
                "Ojos Marrones" to "Lasso",
                "COLMILLO" to "Tainy, Jhayco, Young Miko",
                "La Bachata" to "Manuel Turizo",
                "LALA" to "Myke Towers",
                "Punto 40" to "Rauw Alejandro ft. Baby Rasta",
                "Perro Negro" to "Bad Bunny ft. Feid",
                "Feliz Cumpleaños Ferxxo" to "Feid",
                "Yandel 150" to "Yandel, Feid",
                "Quevedo: BZRP Music Sessions #52" to "Bizarrap ft. Quevedo",
                "El Merengue" to "Marshmello, Manuel Turizo",
                "Monotonía" to "Shakira ft. Ozuna",
                "Mamiii" to "Becky G ft. Karol G",
                "Tacones Rojos" to "Sebastián Yatra",
                "Medallo" to "Blessd, Justin Quiles",
                "Quédate" to "Quevedo",
                "La Jumpa" to "Arcangel, Bad Bunny",
                "Fiel" to "Los Legendarios, Wisin, Jhayco"
            ),
            //  DJ Mix
            "dj_mix" to listOf(
                "Titanium" to "David Guetta ft. Sia",
                "Animals" to "Martin Garrix",
                "Wake Me Up" to "Avicii",
                "Don’t You Worry Child" to "Swedish House Mafia",
                "Lean On" to "Major Lazer, DJ Snake",
                "This Girl" to "Kungs vs Cookin’ on 3 Burners",
                "Turn Down for What" to "DJ Snake, Lil Jon",
                "Clarity" to "Zedd ft. Foxes",
                "Summer" to "Calvin Harris",
                "Party Rock Anthem" to "LMFAO",
                "Sexy and I Know It" to "LMFAO",
                "Where Them Girls At" to "David Guetta ft. Nicki Minaj",
                "Levels" to "Avicii",
                "Without You" to "David Guetta ft. Usher",
                "Scared to Be Lonely" to "Martin Garrix, Dua Lipa",
                "On the Floor" to "Jennifer Lopez ft. Pitbull",
                "Hey Mama" to "David Guetta ft. Nicki Minaj",
                "Don’t Let Me Down" to "The Chainsmokers ft. Daya",
                "Closer" to "The Chainsmokers ft. Halsey",
                "We Found Love" to "Rihanna, Calvin Harris"
            ),

            //  Baladas
            "baladas" to listOf(
                "All of Me" to "John Legend",
                "Someone Like You" to "Adele",
                "Hello" to "Adele",
                "Perfect" to "Ed Sheeran",
                "When I Was Your Man" to "Bruno Mars",
                "Grenade" to "Bruno Mars",
                "Stay With Me" to "Sam Smith",
                "Too Good at Goodbyes" to "Sam Smith",
                "Say Something" to "A Great Big World, Christina Aguilera",
                "Photograph" to "Ed Sheeran",
                "Let Her Go" to "Passenger",
                "Because You Loved Me" to "Celine Dion",
                "My Heart Will Go On" to "Celine Dion",
                "Back to December" to "Taylor Swift",
                "Jar of Hearts" to "Christina Perri",
                "A Thousand Years" to "Christina Perri",
                "Un-break My Heart" to "Toni Braxton",
                "Right Here Waiting" to "Richard Marx",
                "Hero" to "Mariah Carey",
                "Endless Love" to "Lionel Richie & Diana Ross"
            ),

            //  Reggaeton 2023
            "reggaeton_2023" to listOf(
                "Tití Me Preguntó" to "Bad Bunny",
                "Me Porto Bonito" to "Bad Bunny ft. Chencho Corleone",
                "La Jumpa" to "Arcangel, Bad Bunny",
                "Feliz Cumpleaños Ferxxo" to "Feid",
                "Normal" to "Feid",
                "Yandel 150" to "Yandel, Feid",
                "Hey Mor" to "Ozuna, Feid",
                "Perro Negro" to "Bad Bunny ft. Feid",
                "GATÚBELA" to "Karol G, Maldy",
                "Provenza" to "Karol G",
                "Mamiii" to "Becky G, Karol G",
                "LALA" to "Myke Towers",
                "Quevedo: BZRP Music Sessions #52" to "Bizarrap, Quevedo",
                "BZRP Music Sessions #55" to "Bizarrap, Peso Pluma",
                "Vacío" to "Luis Fonsi, Rauw Alejandro",
                "Desesperados" to "Rauw Alejandro, Chencho Corleone",
                "Todo De Ti" to "Rauw Alejandro",
                "Punto 40" to "Rauw Alejandro, Baby Rasta",
                "La Bachata" to "Manuel Turizo",
                "La Nota" to "Manuel Turizo, Rauw Alejandro, Myke Towers"
            ),

            // 👉 Pop
            "pop" to listOf(
                "Bad Romance" to "Lady Gaga",
                "Poker Face" to "Lady Gaga",
                "Halo" to "Beyoncé",
                "Single Ladies" to "Beyoncé",
                "Firework" to "Katy Perry",
                "Roar" to "Katy Perry",
                "Teenage Dream" to "Katy Perry",
                "Shake It Off" to "Taylor Swift",
                "Blank Space" to "Taylor Swift",
                "Style" to "Taylor Swift",
                "Love Story" to "Taylor Swift",
                "Rolling in the Deep" to "Adele",
                "Set Fire to the Rain" to "Adele",
                "Chandelier" to "Sia",
                "Cheap Thrills" to "Sia",
                "Can’t Feel My Face" to "The Weeknd",
                "Starboy" to "The Weeknd",
                "Save Your Tears" to "The Weeknd",
                "Levitating" to "Dua Lipa",
                "Don’t Start Now" to "Dua Lipa"
            ),

            // Cumbia
            "cumbia" to listOf(
                "La Cumbia del Río" to "Los Pikadientes de Caborca",
                "Nunca Es Suficiente" to "Los Ángeles Azules ft. Natalia Lafourcade",
                "Cómo Te Voy a Olvidar" to "Los Ángeles Azules",
                "Mis Sentimientos" to "Los Ángeles Azules ft. Ximena Sariñana",
                "17 Años" to "Los Ángeles Azules",
                "Amor a Primera Vista" to "Los Ángeles Azules, Belinda, Horacio Palencia",
                "El Listón de Tu Pelo" to "Los Ángeles Azules",
                "Tiburon a la Vista" to "Grupo 5",
                "Motor y Motivo" to "Grupo 5",
                "Parranda La Negrita" to "Grupo 5",
                "Apostemos Que Me Caso" to "Hermanos Yaipén",
                "Lárgate" to "Hermanos Yaipén",
                "Oye Mujer" to "Raymix",
                "Te Metiste" to "Ariel Camacho",
                "Llorar y Llorar" to "Los Askis",
                "La Negra Tiene Tumbao" to "Celia Cruz",
                "La Suavecita" to "Rigo Tovar",
                "Cumbia Sampuesana" to "Aniceto Molina",
                "Cumbia sobre el Río" to "Celso Piña",
                "Cumbia Barulera" to "Corraleros de Majagual"
            ),
            //Clásicos
            "clasicos" to listOf(
                "Bohemian Rhapsody" to "Queen",
                "Hotel California" to "Eagles",
                "Stairway to Heaven" to "Led Zeppelin",
                "Imagine" to "John Lennon",
                "Yesterday" to "The Beatles",
                "Hey Jude" to "The Beatles",
                "Billie Jean" to "Michael Jackson",
                "Thriller" to "Michael Jackson",
                "Like a Rolling Stone" to "Bob Dylan",
                "What a Wonderful World" to "Louis Armstrong",
                "Sweet Child O’ Mine" to "Guns N’ Roses",
                "Smoke on the Water" to "Deep Purple",
                "Sultans of Swing" to "Dire Straits",
                "Nothing Else Matters" to "Metallica",
                "Another Brick in the Wall" to "Pink Floyd",
                "Comfortably Numb" to "Pink Floyd",
                "Born to Run" to "Bruce Springsteen",
                "Piano Man" to "Billy Joel",
                "Every Breath You Take" to "The Police",
                "Don’t Stop Believin’" to "Journey"
            ),

            // Salsa
            "salsa" to listOf(
                "Idilio" to "Willie Colón",
                "Pedro Navaja" to "Rubén Blades",
                "Llorarás" to "Oscar D’León",
                "Gitana" to "Willie Colón",
                "Quítate Tú" to "Fania All-Stars",
                "Anacaona" to "Cheo Feliciano",
                "Mi Gente" to "Héctor Lavoe",
                "Periódico de Ayer" to "Héctor Lavoe",
                "El Cantante" to "Héctor Lavoe",
                "Devórame Otra Vez" to "Lalo Rodríguez",
                "Ven Devórame Otra Vez" to "Lalo Rodríguez",
                "Conteo Regresivo" to "Gilberto Santa Rosa",
                "Que Manera de Quererte" to "Gilberto Santa Rosa",
                "Cali Pachanguero" to "Grupo Niche",
                "Una Aventura" to "Grupo Niche",
                "Busca por Dentro" to "Grupo Niche",
                "Me Volvieron a Hablar de Ella" to "Gilberto Santa Rosa",
                "De Qué Callada Manera" to "Andy Montañez",
                "Planté Bandera" to "Andy Montañez",
                "Aquel Lugar" to "Luis Enrique"
            ),

            // Pachanga
            "pachanga" to listOf(
                "La Vida es un Carnaval" to "Celia Cruz",
                "Bailando" to "Enrique Iglesias ft. Gente de Zona",
                "Suavemente" to "Elvis Crespo",
                "La Gozadera" to "Gente de Zona ft. Marc Anthony",
                "Vivir Mi Vida" to "Marc Anthony",
                "Carnaval" to "Maluma",
                "Bamboleo" to "Gipsy Kings",
                "Oye Como Va" to "Santana",
                "Aquel Lugar" to "Eddy Herrera",
                "La Cumbia del Amor" to "Grupo 5",
                "Motor y Motivo" to "Grupo 5",
                "Pégate" to "Ricky Martin",
                "Livin’ La Vida Loca" to "Ricky Martin",
                "La Copa de la Vida" to "Ricky Martin",
                "Colgando en tus manos" to "Carlos Baute, Marta Sánchez",
                "Beso en la Boca" to "Axé Bahia",
                "Danza Kuduro" to "Don Omar, Lucenzo",
                "Taboo" to "Don Omar",
                "Llamado de Emergencia" to "Daddy Yankee",
                "Salió el Sol" to "Don Omar"
            ),

            // Perreo
            "perreo" to listOf(
                "Gasolina" to "Daddy Yankee",
                "Lo Que Pasó, Pasó" to "Daddy Yankee",
                "Rompe" to "Daddy Yankee",
                "Ella y Yo" to "Don Omar ft. Aventura",
                "Dile" to "Don Omar",
                "Virtual Diva" to "Don Omar",
                "Te Boté" to "Nio García, Darell, Casper",
                "Dákiti" to "Bad Bunny, Jhay Cortez",
                "Callaíta" to "Bad Bunny",
                "Safaera" to "Bad Bunny, Jowell & Randy, Ñengo Flow",
                "Baila Baila Baila" to "Ozuna",
                "Caramelo" to "Ozuna",
                "Se Preparó" to "Ozuna",
                "Soy Peor" to "Bad Bunny",
                "No Me Conoce" to "Jhay Cortez, Bad Bunny, J Balvin",
                "Qué Pretendes" to "J Balvin, Bad Bunny",
                "Por Perro" to "Sebastián Yatra, Luis Figueroa",
                "Loco Contigo" to "DJ Snake, J Balvin, Tyga",
                "Taki Taki" to "DJ Snake, Ozuna, Cardi B, Selena Gomez",
                "X" to "Nicky Jam, J Balvin"
            ),

            // Rap
            "rap" to listOf(
                "Lose Yourself" to "Eminem",
                "Not Afraid" to "Eminem",
                "Rap God" to "Eminem",
                "Without Me" to "Eminem",
                "Still D.R.E." to "Dr. Dre, Snoop Dogg",
                "The Next Episode" to "Dr. Dre, Snoop Dogg",
                "California Love" to "2Pac, Dr. Dre",
                "Changes" to "2Pac",
                "Hit ‘Em Up" to "2Pac",
                "Juicy" to "The Notorious B.I.G.",
                "Big Poppa" to "The Notorious B.I.G.",
                "Hypnotize" to "The Notorious B.I.G.",
                "In da Club" to "50 Cent",
                "Candy Shop" to "50 Cent",
                "Many Men" to "50 Cent",
                "Stronger" to "Kanye West",
                "Gold Digger" to "Kanye West",
                "All of the Lights" to "Kanye West",
                "Sicko Mode" to "Travis Scott",
                "Goosebumps" to "Travis Scott"
            )
        )

        val db = FirebaseFirestore.getInstance()

        album.forEach { (albumId, canciones) -> // 👈 cambiado a album
            val albumRef = db.collection("album") // 👈 cambiado a "album"

            albumRef.document(albumId)
                .collection("canciones")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) { // 👈 solo carga si no hay nada
                        canciones.forEach { (titulo, artista) ->
                            val nuevaCancion = hashMapOf(
                                "titulo" to titulo,
                                "artista" to artista
                            )
                            albumRef.document(albumId)
                                .collection("canciones")
                                .add(nuevaCancion)
                        }
                    }
                }
        }
    }
}
