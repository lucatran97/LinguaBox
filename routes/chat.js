var express = require('express');
var router = express.Router();
const rose = require('../rose');
const linguamongo = require ('../linguamongo');

/*handle chat post request*/
router.get('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if(req.query!=undefined&&req.query.message!=undefined&&req.query.email!=undefined&&req.query.language!=undefined){
        rose.inputHandler.processChat(encodeURI(req.query.message), req.query.email.replace(/["]+/g, ''), req.query.language.replace(/["]+/g, ''), res);
    } else {
        res.send(JSON.stringify({message: "Cannot recognize GET request. Maybe missing one of the parameters: message, email and/or language"}));
    }
});

router.post('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if((req.body!=undefined)&&(req.body.message!=undefined)&&(req.body.email!=undefined)&&(req.body.language!=undefined)){
        rose.inputHandler.processChat(encodeURI(req.body.message), req.body.email.replace(/["]+/g, ''), req.body.language.replace(/["]+/g, ''), res);
    } else {
        res.send(JSON.stringify({message: "Cannot recognize POST request. Maybe missing in request body: message, email and/or language"}));
    }
});

module.exports = router;