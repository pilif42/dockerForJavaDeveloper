Project built following https://github.com/docker/labs/tree/master/developer-tools/java/ using Mac OS X El Capitan, Docker Community Edition Version 17.09.0-ce-mac35.


Pulled the required images with: docker-compose -f docker-compose-pull-images.yml pull --parallel


Built a helloworld Docker Image:
	- created a directory called hellodocker
	- inside, created a file Dockerfile
	- docker image build . -t helloworld
	- list the available images with: docker image ls
		REPOSITORY                          TAG                 IMAGE ID            CREATED             SIZE
		helloworld                          latest              c7247cbb0900        34 seconds ago      122MB
		...
	- run the container using the command: docker container run helloworld 


Built a Docker Image using Java:
	- create a new Java project: 
		- use https://start.spring.io/
				- select Maven, Spring Web 
				- get demo.zip
				- copy it to /code_perso/dockerForJavaDeveloper
		- cd /code_perso/dockerForJavaDeveloper
		- unzip demo.zip
		- cd demo
		- mvn clean install
		- run locally and verify the endpoint returns a 204.
	- TODO: stoped at https://github.com/docker/labs/blob/master/developer-tools/java/chapters/ch03-build-image.adoc and 'package this application as a Docker image.' --> attention as my app exposes an endpoint on 8080 and differs from the app in the tutorial.
	 

