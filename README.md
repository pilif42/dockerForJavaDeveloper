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

	- run the OpenJDK container in an interactive manner:
	    - docker container run -it openjdk
	    - java -version
	           - openjdk version "1.8.0_151"
	    - exit

	- package and run this Java application as a Docker image
	    - cd code_perso/dockerForJavaDeveloper/demo
	    - create a Dockerfile
	    - build the image:
	            - mvn clean install
	            - docker image build -t demo-java:latest .
        - run the image:
                - docker container run demo-java:latest
                - you should see the output: ...Started DemoApplication in 5.196 seconds
        - BUT a curl (curl http://localhost:8080/test -v -X GET) from the Macbook will fail because the port 8080 on the container is not linked to the Macbook port 8080.
        - To enable the curl command to work:
                - publish the image:
                        - docker login with brossierp
                        - docker tag demo-java brossierp/demo-java:1.0.1
                        - docker push brossierp/demo-java:1.0.1
                - create a docker-compose-demo-java.yml
                - start container in the background:
                        - cd /code_perso/dockerForJavaDeveloper
                        - docker-compose -f docker-compose-demo-java.yml up -d
                - verify it works:
                        - docker ps --> shows running containers
                        - the curl command now gives a 204.

    - package and run Java Application using Docker Maven Plugin
        - TODO start at https://github.com/docker/labs/blob/master/developer-tools/java/chapters/ch03-build-image.adoc#package-and-run-java-application-as-docker-image
        
