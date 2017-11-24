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
        - used https://github.com/spotify/dockerfile-maven to add the dockerfile-maven-plugin plugin section correctly to my pom.xml
        - uped the version to 1.0.2-SNAPSHOT
        - set up an Artifactory on Docker (to be able to test fully the deploy step):
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
                - To stop and delete the container:
                        - docker stop artifactory
                        - docker rm artifactory
        - cd /dockerForJavaDeveloper/demo
        - mvn clean deploy
            - TODO this errors at the mo:
            [INFO] --- dockerfile-maven-plugin:1.3.6:build (default) @ demo ---
            [INFO] Building Docker context /Users/philippebrossier/code_perso/dockerForJavaDeveloper/demo
            [INFO]
            [INFO] Image will be built as brossierp/demo-java:1.0.2-SNAPSHOT
            [INFO]
            [INFO] Step 1/5 : FROM openjdk:latest
            [INFO] Pulling from library/openjdk
            [INFO] Digest: sha256:b89826260c9f5ebb94ebff7ef23720f2b6de9f879df52e91afd112f53f5f7531
            [INFO] Status: Image is up to date for openjdk:latest
            [INFO]  ---> 377371113dab
            [INFO] Step 2/5 : MAINTAINER Philippe Brossier <brossierp@gmail.com>
            [INFO]  ---> Using cache
            [INFO]  ---> ad5c9a794809
            [INFO] Step 3/5 : ARG JAR_FILE
            [INFO]  ---> Using cache
            [INFO]  ---> e5ab05b5248b
            [INFO] Step 4/5 : COPY target/${JAR_FILE} /usr/src/demoService.jar
            [INFO]  ---> b32414c7646b
            [INFO] Step 5/5 : CMD java -jar /usr/src/demoService.jar
            [INFO]  ---> Running in 3f3bbb69b297
            [INFO]  ---> ec3ea92a54ba
            [INFO] Removing intermediate container 3f3bbb69b297
            [INFO] Successfully built ec3ea92a54ba
            [INFO] Successfully tagged brossierp/demo-java:1.0.2-SNAPSHOT
            [INFO]
            [INFO] Detected build of image with id ec3ea92a54ba
            [INFO] Building jar: /Users/philippebrossier/code_perso/dockerForJavaDeveloper/demo/target/demo-1.0.2-SNAPSHOT-docker-info.jar
            [INFO] Successfully built brossierp/demo-java:1.0.2-SNAPSHOT
            [INFO]
            [INFO] --- maven-install-plugin:2.5.2:install (default-install) @ demo ---
            [INFO] Installing /Users/philippebrossier/code_perso/dockerForJavaDeveloper/demo/target/demo-1.0.2-SNAPSHOT.jar to /Users/philippebrossier/.m2/repository/com/example/demo/1.0.2-SNAPSHOT/demo-1.0.2-SNAPSHOT.jar
            [INFO] Installing /Users/philippebrossier/code_perso/dockerForJavaDeveloper/demo/pom.xml to /Users/philippebrossier/.m2/repository/com/example/demo/1.0.2-SNAPSHOT/demo-1.0.2-SNAPSHOT.pom
            [INFO] Installing /Users/philippebrossier/code_perso/dockerForJavaDeveloper/demo/target/demo-1.0.2-SNAPSHOT-docker-info.jar to /Users/philippebrossier/.m2/repository/com/example/demo/1.0.2-SNAPSHOT/demo-1.0.2-SNAPSHOT-docker-info.jar
            [INFO]
            [INFO] --- maven-deploy-plugin:2.8.2:deploy (default-deploy) @ demo ---
            Downloading: http://localhost:8081/artifactory/libs-snapshot-local/com/example/demo/1.0.2-SNAPSHOT/maven-metadata.xml
            Downloading: http://localhost:8081/artifactory/libs-snapshot-local/com/example/demo/1.0.2-SNAPSHOT/maven-metadata.xml
            Downloading: http://localhost:8081/artifactory/libs-snapshot-local/com/example/demo/1.0.2-SNAPSHOT/maven-metadata.xml
            Uploading: http://localhost:8081/artifactory/libs-snapshot-local/com/example/demo/1.0.2-SNAPSHOT/demo-1.0.2-20171124.211512-1.jar
            Uploading: http://localhost:8081/artifactory/libs-snapshot-local/com/example/demo/1.0.2-SNAPSHOT/demo-1.0.2-20171124.211512-1.pom
            [INFO] ------------------------------------------------------------------------
            [INFO] BUILD FAILURE
            [INFO] ------------------------------------------------------------------------
            [INFO] Total time: 15.848 s
            [INFO] Finished at: 2017-11-24T21:15:12Z
            [INFO] Final Memory: 51M/480M
            [INFO] ------------------------------------------------------------------------
            [ERROR] Failed to execute goal org.apache.maven.plugins:maven-deploy-plugin:2.8.2:deploy (default-deploy) on project demo: Failed to deploy artifacts: Could not transfer artifact com.example:demo:jar:1.0.2-20171124.211512-1 from/to snapshots (http://localhost:8081/artifactory/libs-snapshot-local): Broken pipe (Write failed) -> [Help 1]

            - TODO: verify that my artifacts end up in http://localhost:8081/artifactory/webapp/#/home
                    - the application .jar
            - TODO: verify where the built image ends up brossierp/demo-java:1.0.2-SNAPSHOT

        - TODO start at https://github.com/docker/labs/blob/master/developer-tools/java/chapters/ch03-build-image.adoc#package-and-run-java-application-as-docker-image

