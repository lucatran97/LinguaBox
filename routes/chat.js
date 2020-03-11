var express = require('express');
var router = express.Router();
var microsoftTranslator = require('../microsoft');

/*handle chat post request*/
router.get('/',async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if(req.query.message!=undefined){
        microsoftTranslator.translate(encodeURI(req.query.message), res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize GET request. Maybe missing message parameter?\"}");
    }
});

router.post('/',function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if((req.body!=undefined)&&(req.body.message!=undefined)){
        microsoftTranslator.translate(encodeURI(req.body.message), res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize POST request\"}");
    }
});

module.exports = router;