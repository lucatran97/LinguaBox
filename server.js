// server.js
var express = require('express');
var microsoftTranslate = require('./microsoft.js');

console.log(microsoftTranslate);

var app = express();

var PORT = 3000;

// on the request to root (localhost:3000/)
app.get('/', function (req, res) {
    res.send('<h1>This server is dedicated to the LinguaBox Project</h1>');
});

// On localhost:3000/login
app.get('/login', function (req, res) {
    console.log(req.params("id"));
    res.send('<h1>This is called when an user logs in</h1>');
});

// On localhost:3000/chat
app.get('/chat', function (req, res) {
    console.log(req.param("message"));
    microsoftTranslate.translate(req.param("message"));
    //console.log(microsoftTranslate.translate(req.param("message")));
    //res.send(prepare(microsoftTranslator.translate(req.param("message"))));
});

// Change the 404 message modifing the middleware
app.use(function(req, res, next) {
    res.status(404).send("Sorry, that route doesn't exist. Have a nice day :)");
});

// Start the server in the port 3000
app.listen(PORT, function () {
    console.log('Example app listening on port 3000.');
});