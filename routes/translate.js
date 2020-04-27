var express = require('express');
var router = express.Router();
const microsoftTranslator = require('../microsoft');

/**
 * Handles translate GET requests
 */
router.get('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if(req.query!=undefined&&req.query.message!=undefined&&req.query.language_to!=undefined&&req.query.language_from!=undefined){
        var opts = {stage: "NORM", language_to: req.query.language_to, language_from: req.query.language_from};
        microsoftTranslator.translate(encodeURI(req.query.message), opts, res);
    } else {
        res.send(JSON.stringify({status: "failure", message: "Cannot recognize GET request. Maybe missing one of the parameters: message, language_to and/or language_from"}));
    }
});

/**
 * Handles translate POST requests
 */
router.post('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if((req.body!=undefined)&&(req.body.message!=undefined)&&(req.body.language_to!=undefined)&&(req.body.language_from!=undefined)){
        var opts = {stage: "NORM", language_to: req.body.language_to, language_from: req.body.language_from};
        microsoftTranslator.translate(req.body.message, opts, res);
    } else {
        res.send(JSON.stringify({status: "failure", message: "Cannot recognize POST request. Maybe missing in request body: message, language_to and/or language_from"}));
    }
});

module.exports = router;