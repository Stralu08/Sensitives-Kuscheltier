
#Hardware
##RPi-Konfiguration
###Basiskonfiguration:
- Betriebssystem aufsetzen
- Accounts und Groups einrichten
- Keyboardlayout einstellen

###Netzwerkkonfiguration:
- Interfaces konfigurieren
- Hotspot konfigurieren
- DHCP-Server
- WLAN-Verbindung (falls irgendwann nötig)

##Sensoren und Motoren
###Rauchsensor
- Schaltung entwerfen
- aufbauen und testen

###Drucksensor
- Schaltung entwerfen
- aufbauen und testen

###Servomotoren
- Schaltung entwerfen
- aufbauen und testen

###Problem: Stromversorgung

###Mirkofonsensoren - NICHT verwenden?

#Software
##Server (Python)
###Modul: Netzwerk
- Übertragung von Kommandos
  - Kommandos empfangen
  - Kommandos senden
  - Kommandos interpretieren
- Übertragung von Files
  - Lesen von Files
  - Schreiben von Files
  - Senden von Files (evtl. über separate TCP-Verbindung?)
- Live-Übertragungen (Audio, Video)
  - aufnehmen und sofort senden (evtl. auch über separate TCP-Verbindung?)

###Modul: Media
- Zugriff auf Kamera, Mikrofon
  - Aufzeichnen von Video und Audio
- Übertragung von den aufgenommenen Daten in Echtzeit
- (evtl. zwischenspeichern )
- Wiedergabe von Sounds
- Format?

###Modul: Pin-Ansteuerung
- Sensoren auslesen
- Motoren ansteuern

##Client (Java, App)
###Netzwerk
- Kommandos
  - Senden von Kommandos
  - Empfangen von Kommandos
  - Files senden
  - evtl. Live-Übertragung (redet in Handy-Mikro - Teddy gibt wieder)

###Media
- Sound
  - Aufnehmen
  - Wiedergeben (Dateien)?
  - Wiedergeben (von Stream)
  - Format?
- Video
  - Wiedergeben (von Stream)
- GUI
  - ...
  - separat besprechen
