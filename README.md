<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/kafdrop/images/kafdrop-logo.png" width="90px" alt="logo"/> Kafdrop – Kafka Web UI &nbsp; [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?url=https%3A%2F%2Fgithub.com%2Fobsidiandynamics%2Fkafdrop&text=Get%20Kafdrop%20%E2%80%94%20a%20web-based%20UI%20for%20viewing%20%23ApacheKafka%20topics%20and%20browsing%20consumers%20)
===
[![Price](https://img.shields.io/badge/price-FREE-0098f7.svg)](https://github.com/obsidiandynamics/kafdrop/blob/master/LICENSE)
[![Download](https://api.bintray.com/packages/obsidiandynamics/kafdrop/main/images/download.svg)](https://bintray.com/obsidiandynamics/kafdrop/main/_latestVersion)
[![Build](https://travis-ci.org/obsidiandynamics/kafdrop.svg?branch=master)](https://travis-ci.org/obsidiandynamics/kafdrop#)
[![Docker](https://img.shields.io/docker/pulls/obsidiandynamics/kafdrop.svg)](https://hub.docker.com/r/obsidiandynamics/kafdrop)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/obsidiandynamics/kafdrop.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/obsidiandynamics/kafdrop/context:java)


<em>Kafdrop is a web UI for viewing Kafka topics and browsing consumer groups.</em> The tool displays information such as brokers, topics, partitions, consumers, and lets you view messages. 


<img src="https://github.com/vamsiprasanth/Kafvam/blob/main/types.gif"/> <h1> KafVam - Kafka Message Viewer Desktop UI </h1>

<em>KafVam is a Desktop UI for viewing Kafka topics, messages.You can also poll messages in realtime /from beginning. It also has functionality to describe groups pertained to the topic to check lag. One exciting feature is to filter the messages in realtime based on multiple search criteria.</em>

This project is designed using eclipse RCP3. It's a desktop application that can support few kafka admin functionalities which cant be supported with web UI thereby easing our day to day activities without having to execute and view in the console.


![ScreenShot](https://github.com/vamsiprasanth/Kafvam/blob/main/KafVam.PNG)


# Features
* View Kafka brokers — topic and partition assignments, size
* View topics — partition count, replication status, and custom configuration
* Browse messages - By Offset/ From Beginning , View max of 1000 messages
                  -  Poll realtime messages from latest offset or from beginning
                  - Filter the messages based on search criteria
* View consumer groups — per-partition  offsets, per-partition lag

# Requirements
* Java 7 or newer
* Kafka (version 0.11.0 or newer) 
* Windows 7 or later

# Getting Started
You can directly copy the kafkavam.zip into your folder and configure the kafvam.properties and log4j.properties and run KafVam.exe to launch the application

# Contributing Guidelines
All contributions are more than welcomed. Contributions may close an issue, fix a bug (reported or not reported), add new design blocks, improve the existing code, add new feature, and so on. In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to making participation in our project and our community a harassment-free experience for everyone.

### A special thanks to Kafdrop for inspiring me to do this application.
