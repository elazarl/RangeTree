RangeTree Usage Example
=========

A simple web app, that finds all starbucks coffee shops near a certain
street using the Range tree.

To see it in action, run

    $ ./run.sh

which simply `mvn install` the range tree, and then `mvn jetty:run`
the example.

then browse http://localhost:8080/rangetree-starbucks/ type an address, e.g. washington in the input field, and see how it fetches the starbucks in 5 kilometers range, using the range tree.

Starbucks locations CSV taken from http://www.gpspassion.com/forumsen/topic.asp?TOPIC_ID=67416

to clean up your local repository after running the example (in bash):

    $ rm -rf ~/.m2/repository/com/github/elazarl/rangetree-{tree,example,starbucks}
