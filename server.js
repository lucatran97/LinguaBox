// @ts-check
const express = require('express');
const http = require('http');
const url = require('url');
const request = require('request');

async function main() {
  // Azure App Service will set process.env.port for you, but we use 3000 in development.
  const PORT = 3000;
  
  // Create the express routes
  let app = express();
  
  app.use(express.static('public'));
  // Change the 404 message modifing the middleware

  app.get('/', function (req, res) {
    res.send('<h1>This server is dedicated to the LinguaBox Project</h1>');
    });

  // On localhost:3000/login
    app.get('/login', function (req, res) {
    console.log(req.param("id"));
    res.send('<h1>This is called when an user logs in</h1>');
  });
  
  // On localhost:3000/chat
  app.get('/chat', function (req, res) {
    console.log(req.param("message"));
    //microsoftTranslate.translate(req.param("message"));
    //console.log(microsoftTranslate.translate(req.param("message")));
    //res.send(prepare(microsoftTranslator.translate(req.param("message"))));
  });

  app.use(function(req, res, next) {
    res.status(404).send("Sorry, that route doesn't exist. Have a nice day :)");
    });

  // Create the HTTP server.
  let server = http.createServer(app);
  server.listen(PORT, function () {
    console.log(`Listening on port ${PORT}`);
  });
}

main();
