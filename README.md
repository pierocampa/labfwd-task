# Take-Home Task for Senior Backend Engineer Position
* [Background](#background)
* [The Task](#the-task)
* [Technologies](#technologies)
* [Setup](#setup)
* [Sources](#sources)
* [Testing](#testing)

### Background
Scientists need a digital solution to store and identify various items used in their experiments.
These items come in different forms and might include samples, chemicals, devices, etc...
A usual practice in laboratories is to store the items in categories. Each category has attribute
definitions and each item in those categories should fit into those definitions. You can imagine
categories as database tables while attribute definitions are columns. Items are, in this case, the
rows in those database tables.

### The Task
Please create a REST API application using Java which provides the following functionality:

- Creating categories with attribute definitions
- Creating items in those categories
- Updating items
- Getting items of a category

As described in the background section, you can imagine the above functionality as in visual
database design tools (e.g. MySQL Workbench). You are free to make assumptions and come
up with your own solution. Keep in mind that we do not expect any UI development from you as
this is a pure backend task.

### Technologies

The task has been developed as a Dynamic Web Project in the Eclipse IDE, and makes
use of the following components:

- **Maven**: for compilation, testing and deployment through `pom.xml` file.
- **Jersey** : for the implementation of the RESTful Web Service (JAX-RS API).
- **JUnit** : for writing unit tests.
- **Hibernate** : JPA-compliant management of the persistence.
- **PostgreSQL** : the DBMS.
-  **Tomcat**: the deployed WAR container.

### Setup

Enter the `psql` command line then create the test `labfwd_admin` PostgreSQL role created for the task: 
```sh
$ psql=> CREATE ROLE labfwd_admin WITH CREATEDB LOGIN ENCRYPTED PASSWORD 'labfwd';
```

Now initialize the `labfwd_db` database:
```sh
$ createdb -O labfwd_admin labfwd_db
```

You can now deploy the Web App with Maven:
```sh
$ mvn clean install
```

Deploy the generated WAR into the server then check in your preferred browser the response at:
```sh
http://localhost:8080/LabForwardTask/rest/test/hi
```

### The API

The following RESTful API has been defined for the management of categories:

| HTTP req  | URL | Action |
| ----------- | ----------- | ----------- |
| HTTP POST | */{cat}* | Create a new category labelled *{cat}* and defined by the request content[^1] 
| HTTP DELETE | */{cat}* | Deletes the *{cat}* category (and all the registered items in it)

For items in categories instead:
| HTTP req  | URL | Action |
| ----------- | ----------- | ----------- |
| HTTP GET  | */{cat}* | Get all items of the *{cat}* category.
| HTTP POST | */{cat}/{i}* | Create a new item *{i}* in the *{cat}* category
| HTTP PUT  | */{cat}/{i}* | Creates or replaces an existing item *{i}* in the *{cat}* category
| HTTP PATCH | */{cat}/{i}* | Updates the information of existing item *{i}* in the *{cat}* category
| HTTP DELETE | */{cat}/{i}* | Deletes the item *{i}* in the *{cat}* category

Proper HTTP error codes are generated in case of illegal operations.

### Sources

This project is structured in 3 source packages:

| Package     | Description |
| ----------- | ----------- |
| `io.labforward.jpa`  | Low-level handling (CRUD) of persisted objects. |
| `io.labforward.model`| POJO classes used for data exchange with clients. |
| `io.labforward.rest` | RESTful Servlets. |

Unit tests are also available (and integrated in the Maven compilation) at the usual *src/main/test* folder.

### Testing

Apart from the Unit tests available in the sources, a handy way to test the application is to 
use `curl`:

```sh
$ curl -X GET http://localhost:8080/LabForwardTask/rest/test/hi
```

#### EXAMPLE: Create a new category

Run the following HTTP POST request to create a new `foo` category:

```sh
$ curl -X POST \
     -H "Content-type: text/plain" \ 
     -d '{ "attributes":[ {"uom":"varchar"}, {"value":"float8"} ] }' \
     http://localhost:8080/LabForwardTask/rest/foo
```

The definition of labels and data types of the category are attached as a JSON object, listed as key-value 
pairs under the `attributes` array (see also `foo_cat.son` file for an example).

Verify the successful creation of the new category in the database:
```sh
$ psql -U labfwd_admin labfwd_db
...
$ labfwd_db=> \d foo
                                    Table "public.foo"
   Column    |       Type        | Collation | Nullable |             Default             
-------------+-------------------+-----------+----------+---------------------------------
 id          | integer           |           | not null | nextval('foo_id_seq'::regclass)
 label       | character varying |           | not null | 
 description | character varying |           |          | 
 uom         | character varying |           |          | 
 value       | double precision  |           |          | 
Indexes:
    "foo_pkey" PRIMARY KEY, btree (id)
```

Note that the application automatically adds an auto-incrementing serial identifier `id` to the
table definition, plus a mandatory (unique) label as human-readable identifier (the one to be
used in URLs). 

Now send this DELETE request to drop the toy table:

```sh
$ curl -X DELETE http://localhost:8080/LabForwardTask/rest/foo
```

#### EXAMPLE: Add items to a category

Create the new category as shown in the previous example, then
run the following HTTP POST requests to create a bunch of new items in the category:

```sh
$ cat item.json 
  { "values":[ {"label":"'test-01'"}, {"uom":"'mm'"}, {"value":"13.4"} ] }
$ curl -X POST \
     -H "Content-type: text/plain" \
     -d @item.json \
     http://localhost:8080/LabForwardTask http://localhost:8080/LabForwardTask/rest/foo/test-01 
```

```sh
$ cat item.json 
  { "values":[ {"label":"'test-02'"}, {"uom":"'mm'"}, {"value":"9.2"} ] }
$ curl -X POST \
     -H "Content-type: text/plain" \
     -d @item.json \
     http://localhost:8080/LabForwardTask http://localhost:8080/LabForwardTask/rest/foo/test-02 
```

```sh
$ cat item.json 
  { "values":[ {"label":"'test-03'"}, {"uom":"'mm'"}, {"value":"19.8"} ] }
$ curl -X POST \
     -H "Content-type: text/plain" \
     -d @item.json \
     http://localhost:8080/LabForwardTask http://localhost:8080/LabForwardTask/rest/foo/test-03
```

Now you might want to update an existing element:

```sh
$ curl -X PATCH 
     -H "Content-type: text/plain"
     -d '{ "values":[ {"value":"20.5"} ]}'
     http://localhost:8080/LabForwardTask http://localhost:8080/LabForwardTask/rest/foo/test-03
```

Finally, to get an overview of all inserted items, simply hit an HTTP GET on the category,
either with `curl` or directly on your browser:

```sh
$ curl -X GET http://localhost:8080/LabForwardTask/rest/foo
  { [ 
      {"id":1,"label":"test-01","uom":"mm","value":13.4},
      {"id":2,"label":"test-02","uom":"mm","value":9.2},
      {"id":3,"label":"test-03","uom":"mm","value":20.5}
    ]
  }
``` 

[^1]: Only JSON format is accepted for now.


