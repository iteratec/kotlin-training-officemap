# Sitzplatzreservierung
Bietet die Möglichkeit an, mit dem sich auf dem Tisch befindenden Button den Sitzplatz zu reservieren
## Funktionsumfang

###  Der Button und die Lichter
Beim Drücken des Knopfes wird überprüft, ob der Sitzplatz reserviert ist oder nicht. Es gib drei unterschiedliche Fälle.
+ Sitzplatz ist frei. (Grünes Licht an)
+ Sitzplatz ist Adhocbelegt d.h. schonmal mit dem Button reserviert. (Grünes und Rotes Licht an)
+ Sitzplatz ist normal reserviert. (Rotes Licht an)


___
## Arduino
Beim Starten wird Setup ausgeführt und danach wird loop, wie der
Name schon sagt, als unendliche Schleife ausgeführt. 

### Setup
In setup passiert folgendes
+ WiFi Verbindung wird hergestellt
+ Der aktuelle Status wird nachgefragt 
+ die passenden Lichter dazu werden angezeigt.

### Loop

Sobald der Button gedrückt wird, wird an das Backend eine Anfrage für den Status geschickt und es passiert folgendes.
+ Ist der Sitzplatz frei, dann wird eine Adhocreservierung durchgeführt. 
+ Ist der Sitzplatz adhoc belegt, dann wird er freigegeben. 
+ Ist der Sitzplatz normal reserviert, blinken die Lichter wild und es wird weder Reservierung noch Freigabe ausgeführt.
  
Es wird alle 3 Minuten der Status und die Lichter aktualisiert, unabhängig davon ob der Button gedrückt wird oder nicht.

___
Reservierung, Freigabe und Statusanfrage, alle drei Funktionen sind von der MAC-adresse des Buttons abhängig.