var express = require('express');
var router = express.Router();
var linguamongo = require('../linguamongo');

router.get('/', async function(req, res, next){
  res.setHeader('Content-Type', 'application/json');
  if(req.query!=undefined&&req.query.email!=undefined){
      linguamongo.dbCRUD.signInHandler(req.query.email.replace(/["]+/g, ''), res);
  } else {
      res.send(JSON.stringify({status: "failure", message: "Cannot recognize GET request: Email missing or invalid."}));
  }
});

router.post('/', async function(req, res, next){
  res.setHeader('Content-Type', 'application/json');
  if((req.body!=undefined)&&(req.body.email!=undefined)){
      linguamongo.dbCRUD.signInHandler(req.body.email.replace(/["]+/g, ''), res);
  } else {
      res.send(JSON.stringify({status: "failure", message: "Cannot recognize POST request: Email missing or invalid."}));
  }
});

module.exports = router;
