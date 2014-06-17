#Java Reddit API Wrapper

[![travis-ci build status](https://travis-ci.org/thatJavaNerd/JRAW.svg?branch=master)](https://travis-ci.org/thatJavaNerd/JRAW)

###Contributing
Want to contribute? Fantastic! Follow these steps:

1. Fork the repository
2. Put your testing user's credentials in `/src/test/java/resources/credentials.txt`. The first line should be the username, and the second should be its password.
3. Add your code. Implement an API endpoint, make the code prettier, or even just fix up some whitespace or documentation.
4. Add TestNG tests that implement your code
5. Test your code by executing `./gradlew test`
6. Update [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) by running `./gradlew updateEndpoints`
6. Send a pull request!

###API Endpoints
See [`ENDPOINTS.md`](https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md) for a list of endpoints that need to implemented and ones that have already been implemented.
