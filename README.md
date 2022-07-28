# AndroidProject

! Work in Progress

Ursprünglich Gruppenprojekt für den MATSE Android Kurs, hier Erweiterung meines Teils

- Link zu Erstentwurf in Figma: https://www.figma.com/file/1KVhaKl6HCnynB71GxX0eG/WGApp-Test
- Grundidee: Eine App erstellen, die im Tinder-Format Swiping, Matching und Chat von/zwischen unterschiedlichen Profilen ermöglicht, aber mit dem Ziel, freie WG-Zimmer mit potentiellen neuen Mitbewohnern zu matchen

## Tools/Skills
- Android Studio
- Kotlin
- Firebase
- Git (Gitlab)
- Gradle

## Resources
- https://github.com/yuyakaido/CardStackView
- https://github.com/SimCoderYoutube/TinderClone (Match part copied)
- u.a.

## Infos zur Datenbankanbindung/Testing

Die resultierende App nutzt eine Firebase Database von Google. 
Die App ist darauf ausgelegt, dass die Nutzer bereits in der Datenbank vorhanden sind, da keine Registrierungsmöglichkeit implementiert wurde. Daher sind in der Database 6 Dummy-Nutzer, je drei pro Kategorie (Zimmersuchende vs. WGs), angelegt worden, die nicht gelöscht werden sollten. Durch die Appnutzung kommen Unterknoten zu Connections/Matches und untergeordnete Datenbanken für die Nachrichtenverarbeitung hinzu. Die Möglichkeiten zum Testen ohne manuelles Zurücksetzen in der Datenbank sind daher begrenzt, deswegen kann die App aktuell nicht von Externen getestet werden.
Aktuell sind alle Nutzer (beider Kategorien) gleich ausführlich aufgebaut, und die App-Oberfläche (Profildetails/Profilbearbeitung) ist von der Auswahl der Felder her darauf ausgelegt, dass WGs Mitbewohner suchen. Zukünftig könnte man hier eine Fallunterscheidung machen und entweder direkt zwei Kategorien von Nutzern in der Datenbank schaffen oder die Auswahl der angezeigten/bearbeitbaren Attribute je nach Fall variieren (z.B. Quadratmeterangabe für WGs, was für Mitbewohnerprofile keinen Sinn macht).
Testnutzer (frei erfunden): wg1@test.de bzw. sucher1@test.de, Passwort 123456 (analog wg2, wg3, sucher2, sucher3)

## ToDos
- Registrierung und Login (Anlegen neuer Nutzer, Löschen/Zurücksetzen von Matches...)/Datenbankoperationen erweitern, ggf. probeweise auf lokal (Room) umstellen
- Match & Chat-Funktionalität
- Architektur um-/einbauen (MVVM z.B.)
- Navigation erweitern und mit Fragmenten arbeiten
- u.v.m.
