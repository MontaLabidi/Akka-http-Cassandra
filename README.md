# Akka HTTP Cassandra
A simple (micro)service with simple REST APIs to showcase [Akka HTTP](https://doc.akka.io/docs/akka-http/current/?language=scala)
with [Cassandra]( https://cassandra.apache.org/) using __docker__ and __docker-compose__, and a bonus __Kubernetes__ manifests to deploy on a Kubernetes cluster.


## Pre-requisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker-Compose](https://docs.docker.com/compose/install/)
- [Kubernetes](https://kubernetes.io/docs/tasks/tools/) + [Helm](https://helm.sh/docs/intro/install/)


## Setup Environment

### 1- Configure env.conf file

In order to run, most of the containers needs to have the right credentials to access a Cassandra database.

The file `config/env.conf` contains some default values for a quick setup, 
so please update the file with more convenient credentials before runtime to make the database more secure.

---
The file `config/env.conf` consists of the following:

* `DB_HOST`=The hostname of the Cassandra database
* `DB_PORT`=The port to use to connect to the Cassandra database
* `DB_USER`=The database user to connect with
* `DB_PASS`=The password to use to connect to the Cassandra database. Make sure the password is NOT QUOTED!
* `KEYSPACE`=The keyspace name to be used by the application`

---

### 2- Configure .env file

The `.env` file used to pass variables to the docker-compose files. Make sure to update the variables defined
 there with the correct values.

```
CONTAINER_REGISTRY=some.registry.hub
CASSANDRA_VERSION=3.11.10
IMAGE_TAG=0.1
```

## Run the application with Docker-Compose

### 1- Spin up Cassandra Database

To spin up a Cassandra docker container you need to run the following: 

```
$ docker-compose -f cassandra.yml up -d
```

This will start an instance of cassandra with the specified version as well as bootstrap it with some data.

### 2- Start the Application

This made easy with a single command:
 
```
$ docker-compose up --build
```

The docker image is also equipped with the `cqlsh` tool to access cassandra using the service's container
for debugging purposes.

## Usage

If you're using docker-machine then the API will be available at ``<docker-machine ip>``
 otherwise it should be `localhost`.

With the service up, you can start sending HTTP requests.

#### Get all persons:

First lets see the list of all persons in the DB:

```
$ curl -w "\n" -X GET localhost/persons
```
```
[
    {
        "age": 25,
        "birth_date": "1996-02-12",
        "id": "256fce82-2358-4152-a3ad-990b20bb7c37",
        "job_title": "Software Engineer",
        "name": "Monta"
    }
]
```

#### Get a particular person:

we can get a person just by providing their id:

```
$ curl -w "\n" -X GET localhost/persons/256fce82-2358-4152-a3ad-990b20bb7c37
```
```
{
    "age": 25,
    "birth_date": "1996-02-12",
    "id": "256fce82-2358-4152-a3ad-990b20bb7c37",
    "job_title": "Software Engineer",
    "name": "Monta"
}
```

#### Add a person:

We can add a person by providing the name, age, birth date, and the job title is optional.

```
$ curl -w "\n" -X POST -H "Content-Type: application/json" -d '{ "name": "Jack", "age": 25, "birth_date": "1996-04-12"' localhost/persons 
```
```
{
    "age": 25,
    "birth_date": "1996-04-12",
    "id": "50571374-469e-41de-99f7-a8c0b8988a78",
    "job_title": null,
    "name": "Jack"
}
```
```
$ curl -w "\n" -X GET localhost/persons
```
```
[
    {
        "age": 25,
        "birth_date": "1996-02-12",
        "id": "256fce82-2358-4152-a3ad-990b20bb7c37",
        "job_title": "Software Engineer",
        "name": "Monta"
    },
    {
        "age": 25,
        "birth_date": "1996-04-12",
        "id": "50571374-469e-41de-99f7-a8c0b8988a78",
        "job_title": null,
        "name": "Jack"
    }
]
```

#### Update a person:

We can update a person information by providing the id and the new information:

```
$ curl -w "\n" -X PATCH -H "Content-Type: application/json" -d '{ "name": "Marck", "age": 25, "birth_date": "1996-04-12"' localhost/persons 
```
```
{
    "age": 25,
    "birth_date": "1996-04-12",
    "id": "50571374-469e-41de-99f7-a8c0b8988a78",
    "job_title": null,
    "name": "Mark"
}
```
```
$ curl -w "\n" -X GET localhost/persons/50571374-469e-41de-99f7-a8c0b8988a78
```
```
{
    "age": 25,
    "birth_date": "1996-04-12",
    "id": "50571374-469e-41de-99f7-a8c0b8988a78",
    "job_title": null,
    "name": "Mark"
}
```

#### Delete a person:

To delete a person we just need to provide their id:

```
$ curl -w "\n" -X DELETE localhost/persons/50571374-469e-41de-99f7-a8c0b8988a78
```
```
Deleted person with id 50571374-469e-41de-99f7-a8c0b8988a78
```
```
$ curl -w "\n" -X GET localhost/persons
```
```
[
    {
        "age": 25,
        "birth_date": "1996-02-12",
        "id": "256fce82-2358-4152-a3ad-990b20bb7c37",
        "job_title": "Software Engineer",
        "name": "Monta"
    }
]
```

Add more logic to showcase data processing with Scala.

## Deployment on Kubernetes


### 1 - Install Helm

`Helm` is a Kubernetes package manager. It has both a client and a server side.
The client side is a binary that needs to be installed on the control box

https://helm.sh/docs/intro/install/

NOTE: Make sure to install Helm 3 or later versions

### 2 - Prepare configuration files

#### env.conf

The file env.conf should have all the fields filled out with the correct Cassandra Database credentials in order 
to be able to connect. [Check the previous section](#setup-environment)


### 3 - Create Kubernetes Namespace

Create the Kubernetes namespace using:
```
$ kubectl create namespace <namespace-name>
```


### 4 - Configure Kubernetes Secrets

#### Image Pull Secret

This secret is necessary in order to be able to pull images from the Container Registry if it is private.
```
$ kubectl create secret docker-registry registrycreds --docker-server=<your-registry-server> --docker-username=<your-name> --docker-password=<your-pword> --docker-email=<your-email> -n=<your-namespace>

```

#### Database Credentials Secret

In order to keep secret our Azure credentials, make sure you create a Kubernetes secret for them using:

```
$ kubectl create secret generic dbcreds --from-env-file=config/env.conf --namespace=<namespace>
```

### 5 - Deploying the Application

Make sure to update the `values.yaml` files with the appropriate values.
 
Simply run this command to deploy a Cassandra instance on top the described namespace:
```
$ helm install -f Kubernetes_deployment/cassandra/values.yaml Kubernetes_deployment/cassandra/ --generate-name
```
and then, run the following to deploy the application:
```
$ helm install -f Kubernetes_deployment/simple_microservice/values.yaml  Kubernetes_deployment/simple_microservice/ --generate-name
```

