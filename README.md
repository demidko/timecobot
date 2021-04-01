## `Service`

Kotlin microservice template produces self-executable jar application. For brevity, double-space
formatting is used. [`Clikt`](https://ajalt.github.io/clikt/whyclikt/) is included for parsing
command line arguments. [`Ktor`](https://ktor.io/) is included to mock Digital Ocean healthy checks.

### Usage

Make sure you are signed in to your GitHub account, then just click [`here`](https://github.com/demidko/service/generate) to use template.

### Build

Run command `./gradlew clean test shadowJar` in the repo. Then you can start the application with the `java -jar *.jar` command.

### Deploy to [`Digital Ocean`](https://cloud.digitalocean.com/)

Select repository [`here`](https://cloud.digitalocean.com/apps) to start microservice.



