<img src="https://github.com/vamsiprasanth/Kafvam/blob/master/icons/types.gif" alt="logo"/> KafVam - Kafka Message Viewer Desktop UI
===
<em>KafVam is a Windows Desktop UI for viewing Kafka topics, messages.You can also poll messages in realtime /from beginning. It also has functionality to describe groups pertained to the topic to check lag. One exciting feature is to filter the messages in realtime based on multiple search criteria.</em>

This project is designed using eclipse RCP3. It's a desktop application that can support few kafka admin functionalities which is not supported with existing web UI thereby easing our day to day activities without having to execute and view in the console.


![ScreenShot](https://github.com/vamsiprasanth/Kafvam/blob/master/img/KafVam.png)


# Features
* View Kafka brokers — topic and partition assignments, size
* View topics — partition count, replication status, and custom configuration
* Browse messages - By Offset/ From Beginning , View max of 1000 messages
                  -  Poll realtime messages from latest offset or from beginning
                  - Filter the messages based on search criteria
* View consumer groups — per-partition  offsets, per-partition lag
* Supports Secured Kafka Cluster

# Requirements
* Java 7 or newer
* Kafka (version 0.11.0 or newer) 
* Windows 7 or later

# Getting Started
The project was developed in Eclipse Neon. Download Eclipse RCP3 development tools and import the project and run <b>KafVam.product.launch</b>. Kindly provide the <b>log4jPath</b> and <b>propPath</b> in VM arguments. The sample files log4j.properties and kafvam.properties are checked in the code.

# Running the Product in Windows
You can directly download the contents (zip files) in the deploy folder and follow the ReadMe.txt in deploy folder to run the product/executable.

# Contributing Guidelines
All contributions are more than welcomed. Contributions may close an issue, fix a bug (reported or not reported), add new design blocks, improve the existing code, add new feature, and so on. In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to making participation in our project and our community a harassment-free experience for everyone.
