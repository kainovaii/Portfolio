# DashBot - Uptime Kuma Webhook Discord

DashBot est un bot Discord en Java utilisant [JDA](https://github.com/DV8FromTheWorld/JDA) pour recevoir les notifications d'Uptime Kuma et les publier directement dans un salon Discord sous forme d'embed.

---

## Fonctionnalités

- Recevoir les notifications de statuts des services depuis Uptime Kuma via un serveur HTTP.
- Publier les informations détaillées dans un salon Discord avec embed.
- Supporte plusieurs champs : nom du service, URL, ping, timestamp, etc.
- Utilisation de Slash Commands et boutons personnalisés pour les interactions dans Discord.

---

## Prérequis

- Java 17+
- Maven
- Un bot Discord avec le token et les permissions `Send Messages` et `Embed Links`
- Uptime Kuma configuré pour envoyer les webhooks à votre serveur DashBot

---

## Installation

1. Cloner le projet :

```bash
git clone https://github.com/kainovaii/dashbot.git
cd dashbot
