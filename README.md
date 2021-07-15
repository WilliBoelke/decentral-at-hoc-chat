- [1. Einleitung](#1-einleitung)
- [2. Konzept und Entwurf](#2-konzept-und-entwurf)
- [3. Kommunikation mit dem Modul](#3-kommunikation-mit-dem-modul)
  - [3.1. Allgemein](#31-allgemein)
  - [3.2. Modul Input und Events](#32-modul-input-und-events)
- [4. Request Handling und Routing](#4-request-handling-und-routing)
  - [4.1. Requests](#41-requests)
  - [4.2. Empfang und Verarbeitung von Requests](#42-empfang-und-verarbeitung-von-requests)
- [5. Routing Table](#5-routing-table)
  - [5.1. Aufbau](#51-aufbau)
  - [5.2. Add und Update](#52-add-und-update)
- [6. Senden von Nachrichten](#6-senden-von-nachrichten)
- [7. Filter und Blacklisting](#7-filter-und-blacklisting)
- [8. Beispiel](#8-beispiel)
- [9. Probleme und Fazit](#9-probleme-und-fazit)
  - [9.1. Schwierigkeiten](#91-schwierigkeiten)
  - [9.2. Noch bestehende Probleme in der Implementierung](#92-noch-bestehende-probleme-in-der-implementierung)
- [10. Anhang](#10-anhang)
  - [10.1. **Protokolldefinition](#101-protokolldefinition)

## 1. Einleitung
Im Rahmen des Kurses Technik mobiler Systeme wurde ein At-Hoc, Multi-Hop Routing Protokoll definiert und implementiert. Das Selbs definierte At-Hoc Protokoll (SAP) basiert auf AODV.

Dazu gibt es einen Aufbau von Raspberry Pis die mit LoRa Modulen verbunden sind. 

Auf die Lora Module kann durch die Serielle Schnittstelle zugegriffen werden. Die Module können über den AT Befehlssatz gesteuert werden. Das von uns definierte Protokoll kann im Anhang eingesehen werden und wird hier nicht weiter ausgeführt.  

Ziel ist es dabei ein Chat Anwendung auf Basis dieses Protokolls zu entwickeln. 
## 2. Konzept und Entwurf
In diesem Abschnitt wird das Grundlegende Konzept für meine Implementierung erklärt. 

Das Programm verfügt über unterschiedliche Arten von In- und Outputs. 

Zum einen den In- und Output zur seriellen Schnittstelle, zum anderen der In- und Output zum Benutzer. Ich habe mich in diesem Zusammenhang dazu entschieden Threads zu benutzen, damit sowohl der User-Input als auch der Serielle-Input asynchron laufen können.

Dies hat den Vorteil das eintreffende Requests nicht verpasst werden oder zu spät geantwortet wird. 

Auch Systeminformationen des Lora Modules werden über den Input der Seriellen Schnittstelle asynchron gelesen. Da ich für diese Nachrichten (AT.OK, AT.SENDED …) keiner Verwendung gefunden habe, weil ich bei jedem Senden eine gewisse Zeit warte, werden diese meistens ignoriert (Das hat sich im Laufe der Implementierung geändert, dazu später mehr).

Für das SAP sind weitestgehend die gleichen 3 basis Nachrichtentypen (Requests) vorgestehen (RREQ, RREP und RERR), sowie im AODV RFC beschrieben[^1] dazu kommen eine SendTextRequest, welche für die Chat Anwendung notwendig ist sowie drei Acknowledgments. Diese lassen sich am ersten Byte unterscheiden, die genaue Definition dieser Nahrichten lässt sich in unserer Protokolldefinition finden und soll an dieser stille nicht weiter behandelt werden. 

Für das Verarbeiten eintretender Nachrichten habe ich grob 3 Fälle definiert, diese werden nicht von jeder Request benötigt, aber alle Requests lassen sich damit Abdecken. 

- Request ist für diese Node 
- Request ist von dieser Node
- Request ist für eine andere Node

Welcher dieser Fälle bei einer einkommenden Request vorliegt, kann durch einen Vergleich der Lokalen Adresse, der Destination Adresse und der Origin Adresse der jeweiligen Request leicht ermittelt werden. 

Nicht jeder dieser Fälle kann bei jeder Nachricht auftreten, wird hier aber insofern berücksichtig als das er in der Implementierung eine Methode hat, welche dann ggf. leer bleibt. Das hat zum Vorteil das beispielsweise Broadcasts, welche zurück and die Origin Node gelooped wurden, ohne weiteren Code gedroppte werden, da der „Request von mir“-Fall nicht implementiert ist.

Wird eine Nachricht gesendet, so soll dies ebenfalls in Threads geschehen, ein Thread für jede ausgehende Request. Dies wird zwar schwerer zu debuggen, sollte aber insgesamt besser funktionieren. So können die Retries und die Acknowledgments einfacher implementiert werden. Da einer dieser Threads, je nach Nachricht mehre male wiederholt werden kann, auch kann er einfach bei Ankommende Replies / Acknowledgments benachrichtigt werden, was dann zu einer früheren Abbruchbedingung sorgt.

Zur Kommunikation der verschiedenen Threads untereinander soll ein Event basiertes System zu Einsatz kommen, dass auf den Java PropertyChangeEvents basiert. So können sich Threads als Listener auf anderen Threads registrieren und werden beispielsweise über ankommende Nachrichten benachrichtigt.  

## 3. Kommunikation mit dem Modul
### 3.1. Allgemein 
Die Kommunikation mit dem LoRa Modul Erfolg über die serielle Schnittstelle.

Dazu gibt es drei Klassen SerialInput und SerialOutput sowie die Klasse LoraModule. Die Klasse LoraModule ist für das Aufsetzen, Konfigurieren und neu Startens des Moduls verantwortlich, außerdem initialisiert sie den In- und Output.  Der Input läuft, wie geplant, asynchron und liest von der Seriellen Schnittstelle. Einkommende Nachrichten werden bis zum Carriage-return / Linefeed gelesen. Eine vollständig gelesene Nachricht wird dann so weit verarbeitet, als dass zwischen eintreffenden Request/Replys und Systemnachrichten unterschieden werden kann. 
### 3.2. Modul Input und Events
Meine anfängliche Planung (siehe Kapitel 1) sah nicht vor auf Systemnachrichten (AT.OK,AT.SENDING etc.) zu reagieren.  Dies funktionierte problemlos, beim Loggen stellte ich allerdings fest, dass einige male einen CPU\_BUSY Error auftrat.

Dies führte allerdings nicht dazu das die Anwendung nicht mehr funktionierte. Dennoch entschied ich mich deshalb dazu die Systemnachrichten zu verarbeiten. 


Dafür definiert der Serielle Input nun eine Reihe von Events dazu gehört das Event für einkommende Nachrichten von anderen Nodes, und jeweils eins für die Systemnachrichten AT.OK, AT.SENDED und CPU.BUSY. Andere Objekte / Instanzen können sich dort als Listender registrieren. So Registriert sich der Controller, um Requests zu verarbeiten und der Serielle Output registriert sich, um die Systembenachrichtigungen zu erhalten. 

Nun kann für jedes Schreiben in die Serielle Schnittstelle auf das Passende Event gewartet werden. Der Vollständigkeit halber muss erwähnt werden das dies nicht das CPU\_BUSY Problem behoben hat.

 ## 4. Request Handling und Routing
 ### 4.1. Requests
Da ich bei der Implementierung möglichst Objektorientiert bleiben wollte, habe ich mich dazu entschlossen für jede der Requests eine eigene Klasse zu implementieren. Diese beinhaltet auch den Code zum de- und encoden der jeweiligen Requests in Bytes, sowie vom SAP verlangt. 

Alle Requests, auch die Acks und Replies erben dabei von der Klasse Request. 

Mit dieser habe ich die von uns definierten Requests um ein paar nützliche Parameter erweitert, so kann ich hier die Adresse des letzten Nodes (precursor)  speichern 

welche ich aus dem Header der Nachricht 

vom LoRa Modul auslese und später für Acknowledgments und Replys brauche. 

Auf dieselbe Art werden die Requests um die Next-Hop-Adresse erweitert. Dies wird benutzt, sobald eine Route (für die Request destination) in der Routing-Tabelle gefunden wurde.
### 4.2. Empfang und Verarbeitung von Requests
![](Aspose.Words.503c3347-b03d-49ce-ac8b-9f519e0b8a5e.002.png)Wie im vorherigen Kapitel beschrieben, werden alle eintreffenden Requests und Replies über ein Event an den Controller geleitet. Dieser Benutzt den RequestDecoder, welcher zunächst das erste Byte ausliest und dann die passende Request-Klasse initialisiert und an den Controller zurückgibt. 

Der Controller initialisiert dann wiederum eine Router Instanz, für jede Request gibt es ein eigener Router klasse, die diese, basierend auf origin- und destination Adresse, verarbeite. 

Abbildung SEQ Abbildung \\* ARABIC1 : Forwarding einer SendTextRequest

![](Aspose.Words.503c3347-b03d-49ce-ac8b-9f519e0b8a5e.003.pngWie eingangs beschrieben lässt sich jede eingehende Request in drei Fälle einordnen, diese sind als Abstrakte Methoden in der Router Superklasse definiert, und werden von den jeweiligen Implementierungen überschrieben. Dieser Vorgang ist beispielhaft in Abbildung 1 zu sehen. Dort wird eine eintreffende SendTextRequest verarbeitet. Es werden zunächst di drei Fälle geprüft, es wird erkannt, dass die Request weitergeleitet werden muss. In der toForward Methode wird ein HopAck and den precursor gesendet, dann wird die passende Route aus der Routing Tabelle geholt, die SendTextRequest wird um den Next-Hop ergänzt und an den Dispatcher weitergegeben. 
## 5. Routing Table
### 5.1. Aufbau
Die hier implementierte Routing Table enthält zu jeder (bekannten) destination Node genau eine Route. Diese Route soll die zum Zeitpunkt der Erstellung beste Route zum Ziel sein.

In unserer Protokoll Definition sind für die Routing Table folgende eintrage für jede Route vorgesehen: 

|Dest Add.|Dest Seq|Valid/Invalid Seq |Expired Flag|Hop Count|<p>Next Hop</p><p></p>|Precursors|Lifetime|
| - | - | - | - | - | - | - | - |

Abgelaufene Routen werden in meiner Tabelle vor jeder suchen nach einer Route gelöscht, weshalb ich dies (Expired Flag) nicht speichern extra muss. Zusätzlich Speicher ich für jede destination auch die Broadcast IDs, so muss ich nicht eine weitere Liste mit allen Nodes führen.  Für die Lifetime Speichere ich einen Timestamp, von diesem wird dann, wenn benötigt die verbleibende Zeit berechnet. 
### 5.2. Add und Update
Routen werden wie beschrieben nur dann hinzugefügt, wenn sie besser sind als eine bereits bestehende, oder wenn es bisher keine Route zum Ziel gibt. 

Für die Auswahl der besten Route sind nach AODV zwei Informationen wichtig, die Metrik, in unserem Fall also der Hop Count, und die Sequenz Nummer der destination Node.

Wenn die Sequenznummer größer ist (alt – neu > 0) so wird die Route in jedem Fall gepuderte. Andernfalls entscheidet der Hop Count.[^2] 
## 6. Senden von Nachrichten
Nachdem in einer der Router Klassen eine Requests oder Reply initialisiert wurde, wird diese an die den Dispatcher gegeben, dieser verfügt über drei Methoden zum Senden von Nachrichten.

Eine zum Broadcasten (forwarded RouteRequests), eine zum einzelnen Unicasten (Acks) und eine zum Senden mit Retries. Zum Senden von Retries wird ein Thread gestartet, dieser führt das Senden 3 mal durch und wartet den in der Request definierten (siehe 5.3) Zeitraum zwischen den Tries. 

Jeder dieser Threads wir über jede ankommende Request (ACKs und RREPs) durch ein Event benachrichtigt. 

Er vergleicht dann zunächst ob die Typen der Nachrichten zusammenpassen (z.B. SendTextRequest -> HopAck oder SendTextAck ) und dann die entsprechenden Adressen und oder Sequenznummern. 
## 7. Filter und Blacklisting
Ich habe das SAP um einen Filter für wiederkehrende Nachrichten erweitert. Dies kommt vor allem aus der Test-Phase, in der viele Module genutzt wurden und immer wieder dieselben Nachrichten gesendet wurden. Dies machte das Debuggen / Finden von Fehlern sehr schwer. 

Der Filter sitzt direkt auf dem Seriellen Input, um so eine weitere Verarbeitung direkt zu vermeiden. Gefiltert werden ausschließlich Nachrichten von anderen Nodes, da sich die Systemnachrichten zwangsläufig wiederholen.

Für das Filtern wird ein SHA-256 Hash auf der eingetroffenen Nachricht berechnet und, mit einem Timestamp versehen, gespeichert. Jede eintreffende Nachricht wird ebenfalls gehashed und mit der Liste abgeglichen. 

Eine wiederkehrende Nachricht wird für 50 Sekunden blockiert, dies erweis sich als eine akzeptable Zeitspanne.

Das Blacklisting nach AODV[^3] implementiert und hält nicht antwortende Nodes für 3 Minuten in einer Liste vor. Requests dieser Node werden für den genannten Zeitraum ignoriert.
## 8. Beispiel 
Im Folgenden wird beispielhaft und anhand eines (vereinfachten) Sequenzdiagramms die Funktionsweise der Implementierung dargestellt. Dieses lässt sich, vom Prinzip her, auch auf alle anderen Requests / Replies anwenden.  Das Sequenzdiagram zeigt den Ablauf für den Erhalt (als destination Node) einer Route Request, über die Route Reply bis zum Erhalt des Route Reply ACKs. 

![](Aspose.Words.503c3347-b03d-49ce-ac8b-9f519e0b8a5e.004.png)

1. Der asynchron Laufende SerialInput liest eine Nachricht von der Seriellen Schnittstelle

und löst ein Property-Change-Event mit der Bytecodierten Nachricht aus.

1. Der Controller reagiert das Event und benutzt den Decoder um die Nachricht in ein Request (RouteRequest) Objekt zu decoden.

Anschließend initialisiert er eine passende Router Instanz (RouteRequestRouter) und übergibt ihm die Request. 

1. Der Router erstellt eine neue Route zur Origin Node (allgemein für RouteRequests und RouteReplys). Dann vergleicht er die Zieladresse mit der eigenen und stellt fest, dass es für die eigenen Node bestimmt ist (Zieladresse == Lokale Adresse). 
1. Es wird eine RouteReply initialisiert und an den Dispacheur übergeben.

Der Dispatcher kann Nachrichten direkt senden (Acks), sie Broadcasten (Route Request) oder sie einem TransmissionCoordinator übergeben. 

1. In diesem Fall wird die Nachricht einem TransmissionCoordinator, da in diesem Fall mehrere versuche (3) durchgeführt werden. Dafür läuft ein TransmissionCoordinator in einem eigenen Thread.  Dieser wird die Nachricht drei Mal senden und jeweils die definierte Zeit warten (Diese ist in der Request Klasse hinterlegt, da unterschiedlich für verschiedene Requests)
1. Beim Schreiben in den SerialOutput, liest der SerialInput die AT Systemnachrichten, und publish diese an die Event Listender (in diesem Fall der SerialOutput), der 
1. SerialOutput wartet auf diese.
1. Dann wartet der TransmissionCoordinator (Bis Timeout, Retry oder Reply).
1. Der SerialInput empfängt erneut eine Nachricht und der Controller, als Event Listender, bekommt diese, decodiert sie und bekommt ein Route-Reply-ACK. Es wird eine RouteReplyAckRouter Instanz initialisiert und die Nachricht übergeben. 
1. Router für die Acknowledgments leiten die Request direkt and den Dispatcher weite. Dieser benachrichtigt dann alle, noch laufenden, TransmissionCoordinator Threads. Welche dann überprüfen ob die Reply zu ihrer Request / Übertragung gehört, sollte dies der Fall sein, so wird der Thread beendet und es werden keine weiteren Retries ausgeführt.




## 9. Probleme und Fazit 
Dieses Kapitel beschäftigt sich mit Problemen bei der Implementierung und mit noch bestehenden Problemen in dem Programm. 
### 9.1. Schwierigkeiten 
Bei der Definition unseres Protokolls haben wir uns darauf geeinigt, aus Performance gründen, Bytes für die Übertragungen zu benutzen. Dies verkleinert unsere Pakete erheblich und macht so unser Protokoll performanter. Lieder brachte dies auch einige Probleme bei der Implementierung und vor allem für das Debuggen mit sich. An vielen Stellen konnte nicht direkt erkannt werden, ob es sich um einen Übertragungsfehler oder eine Falsche Kodierung handelte. Ein weiteres Problem ergab sich daraus, dass wir die Bytes Ascii-encoded senden. Dies Führte dazu, dass die von vielen verwendete Methode zum Lesen einer Zeile (aus der seriellen Schnittstelle), dadurch gestört wurde, dass einige Bytes als break oder new-line gelesen wurden, auch hier lag die Vermutung zuerst auf Übertragungsproblemen. 

Im Laufe der Implementierung stelle sich auch heraus, dass wir unsere Definition an manchen Stellen ungenau gehalten haben, dies führte zu schwerwiegenden Missverständnissen. 

Dabei ist vor allem anzuführen, dass der RERR von einigen so interpretiert wurde, dass abwechselnd Sequenznummer und Adresse - und von Anderen so, dass erst alle Adressen und dann alle Sequenznummern geschrieben werden. Wir haben dieses Missverständnis glücklicherweise entdeckt und uns auf eine einheitliche Methode geeinigt. Ich denke jedoch, dass dies gut die Schwierigkeiten der Protokolldefinition zeigt. 
### 9.2. Noch bestehende Probleme in der Implementierung
Zum jetzigen Zeitpunkt bestehen noch einige Probleme in meiner Implementierung des Protokolls. Das bereits beschriebene CPU\_BUSY Problem konnte ich bisher nicht beheben. Ich bin hier dabei die Wartezeiten auf das Event anzupassen, sodass es nicht zu großen Abweichungen von den Im Protokoll definierten Zeiten kommt.

Auch habe ich bei einem längeren Praktischen Test festgestellt, dass es bei meiner Implementierung in einem Multihop-Szenario dazu kommt das sich eine Request exponentiell vervielfältigt. 

Dies liegt an den Retries. Eine Nachricht wird bis zu ihrem Acknowledgments 3-mal gesendet.   

Dasselbe tut der intermediate Node, für 3 versuche von der Origin Node führt dieser insgesamt 9 versuche durch (hier angenommen das kein Acknowledgment kommt). Dies würde sich beim nächsten Hop und dem nächsten intermediate Node wieder verdreifachen. 

Aus Zeitgründen werde ich dieses Problem nicht vollständig beheben können. Ich werde mein Programm aber dahingehend anpassen das intermediate Nodes die Nachricht nur einmal weiterleiten. 


## 10. Anhang
### 10.1. **Protokolldefinition

**Funktionale Anforderungen:**6

1. Das Protokoll soll zum Kurznachrichtenaustausch zwischen zwei Clients genutzt werden können. (+ vllt. Broadcastnachrichten)
1. Das Protokoll soll ein Ad-Hoc-Netzwerk mit 20 Teilnehmern ermöglichen.
1. Das Protokoll soll zum Multi-Hop-Routing der Kurznachrichten fähig sein.
1. Das Protokoll soll das korrekte erhalten von Nachrichten gewährleisten sofern erreichbar

**Protokoll:**

Vereinbart als vereinfachte Abwandlung des reaktiven Teils von AODV. Es soll zusätzlich Auskunft über das Erhalten der Datenpakete garantieren (SEND-TEXT-REQUEST-ACK).

Es werden alle Schritte umgesetzt, welche in AODV Kapitel 6 (https://datatracker.ietf.org/doc/html/rfc3561) definiert werden, ausgenommen:

- Expanding Ring Search bei RREQs (6.4)
- Gratuitous RREPs + Intermediate RREPs (6.6.2) & (6.6.3)
- Hello-Messages (6.9)
- Maintaining Local Connectivity (6.10)
- Local Repairs (6.12)
- Actions after Reboot (6.13) (Aus Zeitmangel bei der Präsentation)
- Interfaces (6.14)
- Subnetze

RREQs MÜSSEN wiederholt werden, bis sie durch einen RREP beantwortet werden, aber max. 3 mal (siehe Parameter).

RREPs MÜSSEN wiederholt werden, bis sie durch einen RREP-ACK bestätigt wurden, aber max. 3 mal (siehe Parameter).

SEND-TEXT-REQUESTs MÜSSEN wiederholt werden, bis sie durch einen SEND-HOP-ACK bestätigt wurden, aber max. 3 mal (siehe Parameter).

**Nachrichten werden als Bytes kodiert:**

(5,11,13,1,Hello)

Bytes: [05 0B 0D 01 48 65 6C 6C 6f]

→        5 11 13  1  H  e  l  l  o

→ (Bytes Encoded mit AT+SEND senden (**OHNE KOMMATA**))








**Messages:**

Abwandlungen der AODV Messages + eigene Messages zum Versenden von Datenpaketen.

**RREQ (broadcast):**

1. Type: 1
1. U-flag
1. Hop Count
1. ID / Broadcast ID / RREQ ID (Aliasnamen für ein und dieselbe ID)
1. Origin Address
1. Origin Sequence Number
1. Destination Address
1. Destination Sequence Number

**RREP: (werden immer acknowledged, AT+DEST=PreviousHopAddr)**

1. Type: 2
1. Hop Count
1. Origin Address
1. Destination Address
1. Destination Sequence Number
1. (übrige) Lifetime in s

**RERR (gesendet wenn kein SEND-HOP-ACK erhalten):**

1. Type: 3
1. DestinationCount
1. unreachable Destination Address
1. unreachable Destination Sequence Number
1. additional Addresses
1. additional Sequence Number

→ 5 und 6 immer abwechselnd

**RREP-ACK (AT+DEST=PreviousHopAddr):**

1. Type: 4

**SEND-TEXT-REQUEST (STR, AT+DEST=NextHopAddr)**

1. Type: 5
1. Origin Address
1. Destination Address
1. Message Sequence Number (Erstellung dieser Nummer definiert jeder selbst)
1. Payload (max. 30 Byte)

**SEND-HOP-ACK (AT+DEST=PreviousHopAddr)**

1. Type 6
1. Message Sequence Number

**SEND-TEXT-REQUEST-ACK (STR-ACK, AT+DEST=NextHopAddr)**

1. Type: 7
1. Origin Address
1. Destination Address
1. Message Sequence Number

**Parameter:**

1. Max Versuche: 3
1. Timeout RREQ: 30s
1. Timeout ACK: 4s - 6s
1. Timeout Payload: TimeoutACK.max \* RouteHopCount
1. Route Lifetime = Route Deletion Time = 3 min = 180 s
1. Blacklist duration  = 3 min = 180 s
1. Adressen 1-20** (z.B. 144 -> 14)

→ Jeder Wert bei den Parametern und bei den Messages ist ein Byte

**Protokoll Beispiel: Node A versendet Nachricht an C**



**Schema: Name(Adresse)  
A(11) B(12) C(13)** 

**Topologie: (A) ----- (B) ----- (C)**

**Node A [RREQ::Broadcast(\*B)] (1, 1, 0, 15, 11, 1, 13, 0)**

**Node B [RREQ::Broadcast(\*A,C)] (1, 1, 1, 15, 11, 1, 13, 0)** 

**Node C [RREP::Unicast(B)] (2, 2, 11, 13, 1, ROUTE\_LIFETIME)**

**Node B [RREP-ACK::Unicast(C)] (4)**

**Node B [RREP::Unicast(A)] (2, 2, 11, 13, 1, ROUTE\_LIFETIME)**

**Node A [RREP-ACK::Unicast(B)] (4)**

-- A hat eine Route zu C  --

-- um zu C zu kommen, muss eine Nachricht an B gesendet werden --

(-- aus der Hopanzahl der RREQ von A und der RREP von C ergibt sich eine Route von B -> C --)

**Node A [SEND-TEXT-REQUEST::Unicast(B)] (5, 11, 13, 1, “Hello”)**

**Node B [SEND-HOP-ACK::Unicast(A)] (6, 1)**

**Node B [SEND-TEXT-REQUEST::Unicast(C)] (5, 11, 13, 1, “Hello”)**

**Node C [SEND-HOP-ACK::Unicast(B)] (6, 1)**

**Node C [SEND-TEXT-REQUEST-ACK::Unicast(B)] (7, 11, 13, 1)**

**Node B [SEND-TEXT-REQUEST-ACK::Unicast(A)] (7, 11, 13, 1)**




**Routing speichern on the fly (Wie oben nur andere Kommentare)** 

**Protokoll Beispiel: Node A versendet Nachricht an C**


**Schema: Name(Adresse)  
A(11) B(12) C(13)** 

**Topologie: (A) ----- (B) ----- (C)**

--  --

**Node A [RREQ::Broadcast(\*B)] (1, 1, 0, 11, 1, 13, 0)**

--  --

**Node B [RREQ::Broadcast(\*A,C)] (1, 1, 1, 11, 1, 13, 0)** 

--  --

**Node C [RREP::Unicast(B)] (2, 2, 11, 13, 1, ROUTE\_LIFETIME)**

-- Für B ergibt sich gültige Route zu C -- 

**Node B [RREP-ACK::Unicast(C)] (4)**

\-- --

**Node B [RREP::Unicast(A)] (2, 2, 11, 13, 1, ROUTE\_LIFETIME)**

--  --

**Node A [RREP-ACK::Unicast(B)] (4)**

-- Für B ergibt sich gültige Route zu A -- 

**Node A [SEND-TEXT-REQUEST::Unicast(B)] (5, 11, 13, 1, “Hello”)**

--  --

**Node B [SEND-HOP-ACK::Unicast(A)] (6, 1)**

\-- --

**Node B [SEND-TEXT-REQUEST::Unicast(C)] (5, 11, 13, 1, “Hello”)**

\-- --

**Node C [SEND-HOP-ACK::Unicast(B)] (6, 1)**

--  --

**Node C [SEND-TEXT-REQUEST-ACK::Unicast(B)] (5, 11, 13, 1)**

--  --

**Node B [SEND-TEXT-REQUEST-ACK::Unicast(A)] (5, 11, 13, 1)**

--  --

**Merkliste an AODV Aktionen von Marcel aber auch vllt für andere interessant um nichts zu vergessen (SORTIERT NACH ABSCHNITTEN | IN BEARBEITUNG):**

**alles folgende rot markierte ist nicht sicher**

**Routing Table:**

- Dest Addr
- Dest seq num
- valid or invalid seq num flag
- valid or invalid/expired route flag
- Hop Count
- Next Hop
- List of Precursors (described in Section 6.2)
- Lifetime / Expiring timestamp

**6.1:**

- include latest dest seq number information about each known node/route
- inc own seq number before sending own RREQ
- before sending own RREP: own seq = max(RREQ.destSeq, own seq) *(if u-flag == 0)*
- inc seq unsigned; signed byte seq = (signed byte)(((unsigned byte) seq) + 1) (reinterpret as signed)
- check if seq is new by signed subtract: z.B. ((Msg.seq - seq) & 0xff) > 0 → NEW
- if link is lost due to RERR or expired, inc dest seq of entry and mark as invalid. DO NOT DELETE ENTRY
- information is updated when
  - route valid: seq newer
  - route invalid seq newer or equal

**6.2:**

- route table entry is created when no corresponding entry is found; information from packet. If no seq or u-flag == 1 → entry.validSeq = false
- table entry updated if:
  - msg.seq newer than entry.seq
  - msg.seq == entry.seq && msg.hopCount + 1 < entry.hopCount
  - entry.validSeq == false
- entry.lifetime = RREP.remaining || ROUTE\_LIFETIME
- after every data packet forwarded lifetime of the following routes is set to ROUTE\_LIFETIME:
  - entry.dest == packet.dest
  - entry.dest == packetDestEntry.nextHop
  - entry.dest == packet.prevHop
- if route == valid → route.lifetime is always updated
- precursors: foreach entry.dest → List of node that might use your route to send to entry.dest because target of our or forwarded RREP

**6.3:**

- originate RREQ when route is unknown, invalid/expired or already waiting for RREP:
  - dest seq is last known or u-flag is set
  - origin seq is own seq number prior changed
  - rreq id / broadcast id = current (unsigned incremented)
  - hop count = 0
- store originated RREQ info to identify node is waiting for RREP and to match RREP with pending RREQ
  - entry expires and node does not wait after TIMEOUT\_RREQ and node may tries the RREQ again if not exceeding MAX\_TRIES
- buffer all data packets FIFO while waiting and drop when MAX\_TRIES was exceeded and no route was discovered

**6.4:**

- IRRELEVANT because we don’t use TTL and expanding Ring Search

**6.5:**

- If RREQ received entry to prevHop is created or updated as defined in 6.2
- Check if node received same RREQ lately. If so, silently drop it.
- Inc RREQ.hopCount
- create or update route to RREQ.origin
  - if RREQ.originSeq is newer than entry.destSeq. Update entry.seq
  - entry.validSeq = true
  - entry.nextHop = RREQ.prevHop
  - entry.hopCount = RREQ.hopCount
- if RREQ processed, reverse route lifetime to RREQ.origin must be updated.
- if node does not create RREP:
  - RREQ.hopCount++
  - RREQ.destSeqNum = max(RREQ.destSeqNum, node.entryForDest.seq)
  - forward RREQ
- if node creates RREP:
  - Discard RREQ and create RREP
  - create gratuitous RREP

**6.6:**

- create RREP if:
  - node == RREQ.dest
  - node is intermediate with active route to RREQ.dest && entry.seq newer or equal to RREQ.destSeq
- copy RREQ.dest and RREQ.originSeq to RREP
- unicast to the next hop toward the originator of the RREQ

**6.6.1: RREP from dest**

- inc own seq number if RREQ.destSeq == incOwnSeq
- define params
  - destSeq = destNode.seq (maybe newly inc)
  - lifetime = ROUTE\_LIFETIME

**6.6.2: RREP from intermediate node**

- update precursors:
  - destRouteEntry.precursors.addIfNew(RREQ.prevHop)
  - routeToRREQOriginEntry.precursors.addIfNew(destRouteEntry.nextHop)
- define params:
  - RREP.destSeq = destRouteEntry.destSeq
  - RREP.hopCount = destRouteEntry.hopCount
  - RREP.lifetime = destRouteEntry.remainingLifetime

**6.6.3:**

- IRRELEVANT because currently we do not support gratuitous RREP

PAGE   \\* MERGEFORMAT- 19 -


[^1]: `  `ADDIN ZOTERO\_ITEM CSL\_CITATION {"citationID":"Ied51VVq","properties":{"formattedCitation":"C. Perkins, E. Belding-Royer, and S. Das, {\\i{}Ad Hoc On-Demand Distance Vector (AODV) Routing} (RFC Editor, July 2003), p. RFC3561 (pp. 7\\uc0\\u8211{}10) <https://doi.org/10.17487/rfc3561>.","plainCitation":"C. Perkins, E. Belding-Royer, and S. Das, Ad Hoc On-Demand Distance Vector (AODV) Routing (RFC Editor, July 2003), p. RFC3561 (pp. 7–10) <https://doi.org/10.17487/rfc3561>.","noteIndex":1},"citationItems":[{"id":35,"uris":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"uri":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"itemData":{"id":35,"type":"report","abstract":"The Ad hoc On-Demand Distance Vector (AODV) routing protocol is intended for use by mobile nodes in an ad hoc network. It offers quick adaptation to dynamic link conditions, low processing and memory overhead, low network utilization, and determines unicast routes to destinations within the ad hoc network. It uses destination sequence numbers to ensure loop freedom at all times (even in the face of anomalous delivery of routing control messages), avoiding problems (such as \"counting to infinity\") associated with classical distance vector protocols.","language":"en","note":"DOI: 10.17487/rfc3561","number":"RFC3561","page":"RFC3561","publisher":"RFC Editor","source":"DOI.org (Crossref)","title":"Ad hoc On-Demand Distance Vector (AODV) Routing","URL":"https://www.rfc-editor.org/info/rfc3561","author":[{"family":"Perkins","given":"C."},{"family":"Belding-Royer","given":"E."},{"family":"Das","given":"S."}],"accessed":{"date-parts":[["2021",6,17]]},"issued":{"date-parts":[["2003",7]]}},"locator":"7-10","label":"page"}],"schema":"https://github.com/citation-style-language/schema/raw/master/csl-citation.json"} C. Perkins, E. Belding-Royer, and S. Das, *Ad Hoc On-Demand Distance Vector (AODV) Routing* (RFC Editor, July 2003), p. RFC3561 (pp. 7–10) <https://doi.org/10.17487/rfc3561>.
[^2]: `  `ADDIN ZOTERO\_ITEM CSL\_CITATION {"citationID":"iQPic1Y3","properties":{"formattedCitation":"Perkins, Belding-Royer, and Das, p. 13.","plainCitation":"Perkins, Belding-Royer, and Das, p. 13.","noteIndex":2},"citationItems":[{"id":35,"uris":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"uri":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"itemData":{"id":35,"type":"report","abstract":"The Ad hoc On-Demand Distance Vector (AODV) routing protocol is intended for use by mobile nodes in an ad hoc network. It offers quick adaptation to dynamic link conditions, low processing and memory overhead, low network utilization, and determines unicast routes to destinations within the ad hoc network. It uses destination sequence numbers to ensure loop freedom at all times (even in the face of anomalous delivery of routing control messages), avoiding problems (such as \"counting to infinity\") associated with classical distance vector protocols.","language":"en","note":"DOI: 10.17487/rfc3561","number":"RFC3561","page":"RFC3561","publisher":"RFC Editor","source":"DOI.org (Crossref)","title":"Ad hoc On-Demand Distance Vector (AODV) Routing","URL":"https://www.rfc-editor.org/info/rfc3561","author":[{"family":"Perkins","given":"C."},{"family":"Belding-Royer","given":"E."},{"family":"Das","given":"S."}],"accessed":{"date-parts":[["2021",6,17]]},"issued":{"date-parts":[["2003",7]]}},"locator":"13","label":"page"}],"schema":"https://github.com/citation-style-language/schema/raw/master/csl-citation.json"} Perkins, Belding-Royer, and Das, p. 13.
[^3]: `  `ADDIN ZOTERO\_ITEM CSL\_CITATION {"citationID":"hXnkKXEb","properties":{"formattedCitation":"Perkins, Belding-Royer, and Das, p. 22.","plainCitation":"Perkins, Belding-Royer, and Das, p. 22.","noteIndex":3},"citationItems":[{"id":35,"uris":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"uri":["http://zotero.org/users/local/73ybcAOK/items/H3XLABFK"],"itemData":{"id":35,"type":"report","abstract":"The Ad hoc On-Demand Distance Vector (AODV) routing protocol is intended for use by mobile nodes in an ad hoc network. It offers quick adaptation to dynamic link conditions, low processing and memory overhead, low network utilization, and determines unicast routes to destinations within the ad hoc network. It uses destination sequence numbers to ensure loop freedom at all times (even in the face of anomalous delivery of routing control messages), avoiding problems (such as \"counting to infinity\") associated with classical distance vector protocols.","language":"en","note":"DOI: 10.17487/rfc3561","number":"RFC3561","page":"RFC3561","publisher":"RFC Editor","source":"DOI.org (Crossref)","title":"Ad hoc On-Demand Distance Vector (AODV) Routing","URL":"https://www.rfc-editor.org/info/rfc3561","author":[{"family":"Perkins","given":"C."},{"family":"Belding-Royer","given":"E."},{"family":"Das","given":"S."}],"accessed":{"date-parts":[["2021",6,17]]},"issued":{"date-parts":[["2003",7]]}},"locator":"22","label":"page"}],"schema":"https://github.com/citation-style-language/schema/raw/master/csl-citation.json"} Perkins, Belding-Royer, and Das, p. 22.