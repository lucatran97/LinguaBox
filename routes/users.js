var express = require('express');
var router = express.Router();
var linguamongo = require('../linguamongo');

router.get('/', async function(req, res, next){
  linguamongo.dbCRUD.listDatabases();
  /*res.setHeader('Content-Type', 'application/json');
  if(req.query!=undefined&&req.query.message!=undefined&&req.query.email!=undefined&&req.query.language!=undefined){
      rose.inputHandler.processChat(encodeURI(req.query.message), req.query.email, req.query.language.replace(/["]+/g, ''), res);
  } else {
      res.send(JSON.stringify({message: "Cannot recognize GET request: Email missing or invalid."}));
  }*/
});

router.post('/', async function(req, res, next){
  /*res.setHeader('Content-Type', 'application/json');
  if((req.body!=undefined)&&(req.body.message!=undefined)&&(req.body.email!=undefined)&&(req.body.language!=undefined)){
      rose.inputHandler.processChat(encodeURI(req.body.message), req.body.email, req.body.language.replace(/["]+/g, ''), res);
  } else {
      res.send(JSON.stringify({message: "Cannot recognize POST request: Email missing or invalid."}));
  }*/
});

module.exports = router;
