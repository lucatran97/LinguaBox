// server.js

var express = require('express');

var app = express();

var PORT = 3000;

// on the request to root (localhost:3000/)
app.get('/', function (req, res) {
    res.send('<h1>This server is dedicated to the LinguaBox Project</h1>');
});

// On localhost:3000/welcome
app.get('/login', function (req, res) {
    console.log(req.param("id"));
    res.send('<h1>This is called when an user logs in</h1>');
});

// Change the 404 message modifing the middleware
app.use(function(req, res, next) {
    res.status(404).send("Sorry, that route doesn't exist. Have a nice day :)");
});

// start the server in the port 3000 !
app.listen(PORT, function () {
    console.log('Example app listening on port 3000.');
});