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

