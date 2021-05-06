## Elektrotehnički fakultet Banja Luka

## Programski jezici 2

# Projektni zadatak

- maj 2019 -

U zamišljenoj državi Java potrebno je napraviti jednostavan sistem za kontrolu vazdušnog
saobraćaja. Kontrola vazdušnog saobraćaja odnosi se na praćenje trenutne situacije u vazdušnom
prostoru države i otkrivanje letjelica koje ugrožavaju taj prostor. Sistem koji treba da podrži
navedene aktivnosti sastoji se iz dijela za osmatranje vazdušnog prostora i aplikacije za upozoravanje
i prikaz trenutnog saobraćaja (dalje u tekstu: glavna aplikacija). Osim toga, potrebno je napraviti i dio
za simuliranje događaja kako bi se navedene aplikacije mogle testirati (dalje u tekstu: simulator).

U vazdušnom prostoru mogu se naći letjelice i rakete. Letjelice mogu biti avioni, helikopteri ili
bespilotne letjelice. Svaka letjelica ima model, jedinstvenu identifikacionu oznaku, visinu i brzinu
leta, karakteristike (proizvoljan broj podataka u formatu ključ - vrijednost) i osobe koje se nalaze u
letjelici. Osobe mogu biti putnici i piloti. Osobe imaju ime i prezime, putnici imaju broj pasoša, a
piloti imaju licencu za letenje. Avioni se dijele na transportne, putničke, vojne i protiv-požarne
avione. Transportni avioni prevoze terete bilo koje vrste, a potrebno je definisati maksimalnu težinu
tereta koji se prevozi. Putnički avioni imaju broj sjedišta i maksimalnu težinu prtljaga koji prevoze.
Protiv-požarni avioni mogu gasiti požare i mogu da nose određenu količinu vode za gašenje požara.
Vojni avioni se dijele na lovce i bombardere. Svi vojni avioni imaju mogućnost nošenja naoružanja i
napada na ciljeve. Bombarderi imaju mogućnost bombardovanja ciljeva na zemlji, a lovci imaju
mogućnost gađanja ciljeva i na zemlji i u vazduhu. Helikopteri se dijele na transportne, putničke i
protiv-požarne. Transportni helikopteri prevoze terete bilo koje vrste, a potrebno je definisati
maksimalnu težinu tereta koji se prevozi. Putnički helikopteri imaju broj sjedišta. Protiv-požarni
helikopter može da gasi požar i može da nisu određenu količinu vode za gašenje požara. Bespilotna
letjelica može snimati teren. Rakete imaju atribute domet, visinu i brzinu letenja, a mogu biti vojne ili
rakete protivgradne zaštite. Brzine letenja se definišu na slučajan način, a u simulaciji to vrijeme
predstavlja vrijeme koje objekat provede na jednom polju (1-3s).

Simulator je program koji ima zadatak da konstantno simulira let civilnih letjelica, tako što svakih _n_
(0-5) sekundi kreira jednu civilnu letjelicu tipa odabranog na slučajan način koja započinje letenje na
matrici dimenzija 100x100. Matricu treba posmatrati kao vazdušni prostor (kao mapa, bez prikaza
visine), pri čemu letjelica u taj prostor ulazi na slučajno odabranoj strani i kroz prostor leti
pravolinijski do kraja (npr. letjelica uđe na gornjoj strani na poziciji 0, 5 i leti do „dna“, do pozicije
100, 5). Kada letjelica dođe do kraja prostora, ona više nije od interesa za simulaciju. Ukoliko se na
istoj poziciji istovremeno nalazi više od jednog objekta bilo koje vrste na istoj visini, tada dolazi do
sudara i sve letjelice nestaju iz vazdušnog prostora. Simulator koristi fajl _config.properties_ za
definisanje svih parametara (uključujući dimenzije, intervale kreiranja letjelica i slično). Jedan
parametar treba da se odnosi na prisustvo stranih vojnih objekata, a drugi na prisustvo domaćih
vojnih objekata. Ovi parametri se na početku simulacije postavljaju na negativne vrijednosti. Ako se
tokom trajanja simulacije neki od parametara postave na pozitivnu vrijednost (ručno u _config.
properties_ fajlu), tada aplikacija generiše odgovarajuću vojnu letjelicu koja ulazi u vazdušni prostor.


Ako je letjelica strana (ovo se određuje na osnovu atributa objekta), onda sve ostale letjelice
napuštaju vazdušni prostor najkraćim putem. Mogućnost zabrane letenja u vazdušnom prostoru se
izvršava na isti način, i ova opcija mora biti dostupna u bilo kom trenutku izvršavanja simulacije.

Sistem za osmatranje vazdušnog prostora (radarski sistem) konstantno prati stanje u vazdušnom
prostoru (dijeli matricu sa simulatorom). Podaci iz vazdušnog prostora se mapiraju u tekstualni fajl
_map.txt_ , gdje se na određen način evidentira prisustvo letjelica na nekoj poziciji i kratak opis tih
letjelica (samo ključni podaci). Ovaj fajl se ažurira svakih _n_ sekundi. Vrijednost za _n_ i ostali parametri
trebaju biti u _radar.properties_ fajlu.

Ako radar prepozna stranu vojnu letjelicu u vazdušnom tada se kreira tekstualni fajl čiji naziv je
jednak trenutnom vremenu i u njega upisuje sve potrebne informacije. Fajlovi se nalaze u folderu
_events_. Nakon kreiranja tekstualnog fajla sa informacijama, radar šalje signal simulatoru po pošalje
dva domaća vojna aviona - lovca na detektovanu stranu vojnu letjelicu. Domaći avioni treba da idu
paralelno sa stranom letjelicom, jedan sa lijeve, a drugi sa desne strane. Kada se domaći i strani
avioni sretnu, tada domaći avioni uništavaju stranu letjelicu.

Ako se detektuje sudar, tada se pravi objekat upozorenja koji ima tekstualne podatke opis, vrijeme i
poziciju sudara i taj fajl se serijalizuje u folder _alert_. Voditi računa o performansama radara.

Sistem za osmatranje vazdušnog prostora i glavna aplikacija komuniciraju tako što dijele podatke
preko zajedničkih fajlova. Glavna aplikacija je GUI aplikacija koja na početnom ekranu ima prikaz
matrice koja prikazuje trenutno stanje u vazdušnom prostoru. Na matrici se prikazuju podaci
dobijeni iz odgovarajućeg tekstualnog fajla i oni se konstantno osvježavaju. Voditi računa da se na
efikasan način prikažu svi važni podaci (npr. tipovi letjelica određenim bojama i slično). Osim toga,
aplikacija ima i opciju za aktiviranje, odnosno deaktiviranje zabrane letenja. Glavna aplikacija treba
da prati da li je negdje došlo do sudara i ako jeste, tada treba da iskoči novi prozor na kome su
prikazani svi detalji. Osim toga, korisnik glavne aplikacije treba da ima opciju da u novom prozoru
prikaže sve sudare (lista i detalji). Kada se u _events_ folderu pojavi novi fajl, tada se na glavnoj
aplikaciji ispod matrice pojavljuje tekstualna poruka o ovom događaju (kao labela). Potrebno je
napraviti novi prozor za prikaz svih događaja (lista i detalji) iz _events_ foldera.

Sistem za kreiranje kopije podataka je program koji svakih 60 sekundi pravi kopiju svih tekstualnih
fajlova tako što ih čuva u ZIP fajlu čiji naziv je u formatu:
_backup_godina_mjesec_datum_sat_minut.zip_.

Obavezno koristiti _Logger_ klasu za obradu izuzetaka u svim klasama.

