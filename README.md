# TDP028 - Qfilm - Android Movies Application

Qfilm is an application that displays movies and series in the category "lgbt". The user can browse lists based on genre and search
for the movie or series they are looking for. Each movie and series has a page with detailed information where the user can read an overview, 
watch trailer and see images. If the user signs in they can create their own collections and add movies/series to them.

# List of demands 

# Technical 

* Modulär kod: MVVM Design pattern enligt https://developer.android.com/jetpack/guide 

* Användning av fragments vs activites: Jag har mest använt fragment. Jag har till exempel använt fragment för det som visas när användaren navigerar
via bottom navigeringen eftersom jag ville att "state" på sidorna skulle bevaras och inte laddas om i och med att en ny aktivitet startas. Jag antog
även att användning av fragment skulle underlätta vid anpassning till tablets. På "settings" sidan har jag istället använt en ny aktivitet och det 
är för att jag ville starta om aktiviteten för att vissa inställnignar som ändras skulle visas för användaren direkt, t.ex. förändring av tema.

* Hantering av små/stora skärmar: Jag utgick från en layout som passar till mobiltelefoner och sedan har jag gjort en del justeringar för 
tablets. Recyclerviews ändras till att använda GridLayoutManager istället för LinearLayoutManager alternativt får fler kolumner, vissa fragment
visas som DialogFragments t.ex. DetailPageFragment och text-storlek och marginaler ändras i och med större skärm.

* Hantering av användarinput: Den användarinput som finns är via EditTexts eller klick på Buttons.

* Användning av callbacks: callbacks används i samband med interaktion med firebase (Se FirebaseAuthViewModel/FireStoreViewModel) och olika 
ClickListeners. Oklart vad ni menar med snygg användning, men t.ex. så är ClickListeners satta i Java kod och det har gått bra att testa sakerna med Mocks.


* Landskaps layout - todo! tanken är att tablets ska finnas i landskapsvy, i nuläget är det bara TrailerFragment som kan få ändras när användaren
roterar skärmen. 

* Användning av interface vid inkoppling av fragment till aktivitet: Jag har ett NavigationInterface som implementeras av MainActivity och via 
vilka metoder de övriga fragmenten kan navigera. MainActivity använder sedan en annan klass 'NavigationUtil' för att utföra fragment transactions 
samt hantera bakåtnavigering.
**Se** ui/utils/navigation 


* Hantering av bakåtknapp - Jag har hanterat detta i NavigationUtil, se metoderna popBackStack() och addToBackStack() samt i NavigationTest klassen. 
**Se** ui/utils/navigation


* Adaptrar: Jag har en bas klass för de adaptrar som används i applikationen. Denna utökas (extends) sedan för att olika datatyper ska kunna
användas. **Se** ui/adapters


# Apis

* Använder Firebase som databas: Jag använder firestore där jag lagrar filmer/serier inom lgbt. Dessa laddas från TMDB och uppdateras via Cloud functions enligt förändringar som sker. Se functions mappen. Huvudanledningen till varför jag valde detta var för att TMDB:s sökfunktionalitet inte 
kan filtrera resultat, vilket var något jag behövde. Jag lagrar också referenser till dessa dokument i användarnas collections vilket
gör det möjligt att hämta olika data beroende på språkinställning. I slutändan valde jag också att ändra så att appen hämtar data härifrån eftersom 
det resulterade i att sidorna blev mer jämna storleksmässigt. När data hämtades från tmdb så filtrerade jag bort de resultat som inte hade vissa data varav hanteringen av pagination osv blev lite mer oklar. 

* Multispråk-stöd: Appen har två språk; spanska och engelska. Användaren kan välja språk på inställningar. Valet lagras i shared preferences
och appliceras genom att "Locale" ändras för contexten innan en aktivitet startas. Se ui/utils/SettingsManager och i aktiviteterna där "attachBaseContext" overridas. 

* Användaren kan logga in med mail och lösenord via firebase **Se** ui/fragments/SignInEmailFragment and viewmodels/FirebaseAuthViewmodel

* Användaren kan logga in via facebook firebase **Se** ui/fragments/SignInFragment and viewmodels/FirebaseAuthViewmodel

* Användaren kan logga in via google firebase **Se** ui/fragments/SignInFragment and viewmodels/FirebaseAuthViewmodel

* Användaren kan spara listor av filmer/serier som lagras i firestore via deras user id. 

* Användaren måste vara inloggad för ovanstående funktionalitet.  

* Performance monitoring används med 3 custom traces: hämtning av detaljinformation (DetailPageFragment), hämtning av lista inom ny genre (ListingFragment) och hämtning av ny sida (ListingFragment).

* Jag har Remote config med variabler för time limits för hur längde data får lov att va kvar i Room så att det kan ändras om det skulle behövas, 
och en lista av genre id:n som exkluderas för tillfället. Detta var för att de knappt hade resultat, men det kan ändras tänker jag. 

* Cloud functions används för att uppdatera innehållet av filmer och serier i firestore. Detta synkas även med Algolia  https://www.algolia.com/ vilka erbjuder bättre sökfunktionalitet än Firestore. Det finns också en funktion som hämtar filmer och serier när en användaren
vill se någon utav sina listor.

# Testning

Jag har hunnit skriva enhetstester men inga integrationstester, t.ex. se till så att saker i firestore deletas som det ska utan det har 
jag bara gjort manuellt. Jag har testat med hjälp av tre emulatorer: Pixel 3a Api29 och 10.1 WXGA Api29 och 7 WSVGA Api29. Jag har haft problem 
med vissa UI tester framför allt de större tex NavigationTest och ListingFragmentTest på grund av att IdingResource laggar. 

