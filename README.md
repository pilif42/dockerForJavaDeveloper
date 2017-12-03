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

    - package and run Java Application using Docker Maven Plugin:
        - used https://github.com/spotify/dockerfile-maven to add the dockerfile-maven-plugin section correctly in my pom.xml
        - uped the version to 1.0.2-SNAPSHOT
        - set up a local Artifactory on Docker (to be able to fully test the deploy step):
                - Pulling the Artifactory Pro Docker Image: docker pull docker.bintray.io/jfrog/artifactory-pro:latest
                - To keep your data and configuration once the Artifactory Docker container is removed, you need to store them on an external volume mounted to the Docker container. There are two ways to do this:
                        - Using Host Directories: see https://www.jfrog.com/confluence/display/RTF/Installing+with+Docker
                        - Using a Docker Named Volume:
                                - docker volume create --name artifactory5_data
                                - create the directory /Users/philippebrossier/devtools/jfrog/artifactory
                - Running Artifactory Pro in a container: docker run --name artifactory -d -v artifactory5_data:/Users/philippebrossier/devtools/jfrog/artifactory -p 8081:8081 docker.bintray.io/jfrog/artifactory-pro:latest
                - To verify that it works:
                        - in Chrome, go to localhost:8081 and follow the set up guide which pops up.
                        - I signed up for a 30-day free trial license.
                        - I kept the default admin user & pwd: User = admin, Password = password
                        - I skipped 'Configure a Proxy Server'.
                        - For 'Create Repositories', I chose: Docker, Maven
                        - push an artifact using the REST API:
                                - cd /dockerForJavaDeveloper/demo
                                - mvn clean install
                                - cd target
                                - curl -u admin:password -X PUT "http://loalhost:8081/artifactory/libs-snapshot-local/com/sample/demo-1.0.2-SNAPSHOT.jar" -T demo-1.0.2-SNAPSHOT.jar
                                - in Chrome, go to http://localhost:8081/artifactory/libs-snapshot-local/com/sample/ and verify the .jar is there.
                                - or in Chrome, go to http://localhost:8081/artifactory/webapp/#/home
                - To start, stop and delete the container:
                        - docker start artifactory
                        - docker stop artifactory
                        - docker rm artifactory
        - deploy with Maven:
                - edit settings.xml under /Users/philippebrossier/.m2 to use the correct credentials for this Artifactory
                - edit settings.xml under /devtools/apache-maven-3.5.0/conf/:
                        - add the correct <profile> section: use the 'Set Me Up' link in the home of the Artifactory web UI to generate what is required.
                - edit your project pom.xml:
                        - add the correct <distributionManagement> section: use again the 'Set Me Up' link in the home of the Artifactory web UI.
                - cd /dockerForJavaDeveloper/demo
                - mvn clean deploy
                - verified that demo-1.0.2-....jar ends up in libs-snapshot-local at http://localhost:8081/artifactory/webapp/#/home
                - manual steps taken as the mvn deploy was failing to push to Docker Cloud. I wanted to prove that manually, it does work.:
                        - cd /dockerForJavaDeveloper/demo
                        - mvn clean install
                        - docker images | grep demo
                                - I can see: brossierp/demo-java 1.0.2-SNAPSHOT ... 2 minutes ago
                        - docker login with brossierp
                        - docker push brossierp/demo-java:1.0.2-SNAPSHOT
                        - log into https://cloud.docker.com/ with brossierp
                            - I can see my new tag 1.0.2-SNAPSHOT under repo brossierp/demo-java
                - TODO mvn clean deploy still fails:
                        - TODO Could not push image: denied: requested access to the resource is denied
                - TODO set up my docker-local repo correctly with:
                        - TODO log in webUI --> artifacts --> select docker-local and set me up
                        - TODO https://www.jfrog.com/confluence/display/RTF/Docker+Registry
                - TODO go back to the pom.xml: see zzz
                - TODO: in settings.xml of Maven: add sth along the lines of
                TODO <server>
                TODO   <id>registry.cn-hangzhou.aliyuncs.com</id>
                TODO    <username>hi34221235@aliyun.com</username>
                TODO    <password>123456</password>
                TODO  </server>

        - TODO start at https://github.com/docker/labs/blob/master/developer-tools/java/chapters/ch03-build-image.adoc#package-and-run-java-application-as-docker-image

