#Designentscheidungen
die auf jeden Fall zu treffen sind!

##App
###Funktion: Video-Live-Stream
Video-Daten werden vom Raspberry auf das Smartphone gestreamt, soweit ist alles klar. Das Ganze wird eine eigene Seite in der App bekommen "Teddys Augen" (noch änderbar).
Wir sind uns also einig über: 
- eine Seite in der Navigation für die Video-Live-Stream-Funktion ("Teddys Augen")
- ein Button, um einzelne Bilder aus dem Stream auf dem Smartphone zu speichern
- Drehung des Kopfes (durch Slider oder Buttons...)

Nun jedoch einige Fragen, über weitere mögliche Funktionen und Antwortmöglichkeiten:
- soll der Video-Stream beim Öffnen der Seite automatisch starten?
  - 1.: Ja
  - 2.: Nein, ein Button muss gedrückt, um die Wiedergabe zu starten werden.
- soll gleichzeitig auch Audio gestreamt werden?
  - 1.: Ja
  - 2.: Nein
  - 3.: Naja: mit zusätzlichem Button aktivierbar

###Funktion: Babyfon
Die Babyfon-Funktion ist nicht gänzlich klar, aber was klar ist, ist folgendes:
- Audio-Stream vom Teddy zum Handy in Echtzeit, und sofortige Wiedergabe

Es gibt wieder ein paar noch offene Fragen:
- Automatischer Start der Aufnahme (und somit Wiedergabe), oder manuell (ähnliche Frage wie beim Video-Stream)
- Speichern am Handy? 
  - Ja
  - Nein
  - aktivierbar (dann wird nur Aufnahme im Zeitraum von Aktivierung zur Deaktivierung oder Verbindungsabbruch gespeichert)
- auch Video-Stream? Nötig, wenn es auch schon Punkt "Teddys Augen" gibt?

###Funktion: Audio-Übertragung (auf Server)
Wie besprochen soll diese Funktion ebenfalls eine eigene Seite in der App bekommen ("Teddys Stimme").
Hier haben wir nun mindestens 2 Möglichkeiten:
- Aufnahme von Sounds und Übertragung auf Server - wird dem Pool von abspiebaren Dateien hinzugefügt
Sollen diese Dateien auch am Smartphone abspielbar sein? Sollen diese Dateien auch vom Smartphone aus auf dem Raspberry wiedergegeben werden? Man soll sie jedenfalls löschen und umbenennen können. Bei 2 Dateien mit gleichem Namen... wie verfahren? (Überschreiben, Dialogfenster, ...?)
- Wiedergabe von Aufnahmen des Handymikros live (also Audio vom Handy auf Raspberry streamen und dort wiedergeben)

###Funktion: Einstellungen
Wir sollten noch eine weitere Option in der Navigation zur Vefügung stellen: Einstellungen
Diese sollten vlt. nur für die Eltern sichtbar sein (z.B. durch PIN geschützt oder so). Jedenfalls wäre es auch zum Testen sehr hilfreich (da könnten wir auch Verbindungsinfos anzeigen lassen, Soundqualität einstellen, usw.) - nur so eine Idee.

##Server + Elektronik
###Funktion: Drucksensor
... WAS genau soll durch den Drucksensor nun passieren?
Darüber sind wir uns, denke ich nicht einig...
Mögliche Verwendungsarten...
- Sprachausgabe: beim Druck wird eine zufällige Audiodatei (aus einem bestimmten Pool von Audiofiles) wiedergebeben
- Spracheingabe (und Antwort...?): ursprüngliche Funktion, wäre weitaus komplexer...
Beim Druck auf den Sensor wird die Sound-Aufnahme gestartet, beim Loslassen beendet. Dann wird das Aufgenommene von einer Sprach-Erkennungs-Bibliothek interpretiert und je nachdem welche Wörter gesagt wurde, kann man entsprechend darauf reagieren (mit einer Soundausgabe zum Beispiel)
- 3.Möglichkeit: Kompromiss: man kann die Funktion in den Einstellungen der App umstellen, der Vorteil ist, dass wir dann noch weitere Funktionalitäten für den Drucksensor realisieren können (z.B. Reaktionstest: der Teddy sagt etwas, dann muss man drücken, die Zeit wird gemessen oder ähnliches)

###Funktion: Rauchsensor
... WAS genau soll der Rauchsensor auslösen?
Möglichkeiten:
- Soundausgabe, der Teddy gibt eine bestimmte Meldung aus
- Warnung in der App (?)
- beides

###Funktion: Stromversorgung
Gut, zugegeben von diesem Punkt habe ich (Komon) im Moment keine Ahnung. Der Raspberry selbst soll durch eine Powerbank versorgt werden, das ist grundsätzlich mal okay. Dadurch gibt es aber wieder einige Probleme:
- Aufladen des Teddys:
Wie lädt man die Powerbank eigentlich wieder auf? Muss man sie dafür vom Raspberry abstecken (sprich: kann man sie während dem Betrieb laden?)? Wo befindet sich die Powerbank? Wird es ein "Fach" (z.B mit Klettverschluss oder Ähnlichem) geben, welches man öffnen kann und die Powerbank anstecken kann?
- Akku leer/schwach:
Was soll passieren wenn der Akku leer wird? Die selbständige Überprüfung der Powerbank selbst ("Akkustandanzeige in der App oder Soundausgabe bei geringem Akkustand") wäre eine Möglichekeit (keine Ahnung ob umsetzbar...). Was passiert jedoch wenn der Akku aber wirklich leer wird? 
- generelle Stromversogung
Wir wissen nicht, ob die Pins des Raspberrys für die Stromversorgung der Schaltungen ausreicht... Wenn nicht: Externe aufladbare Stromquelle? Vlt. sogar einfach 2. Powerbank?

###Funktion: Servomotoren
Hier haben wir sowieso überhaupt keine Ahnung. Was für Motoren haben wir? Wie steuern wir die an? Stromversorgung? 