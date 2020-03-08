var express = require('express');
var router = express.Router();
var microsoftTranslator = require('../microsoft');

/*handle chat post request*/
router.get('/',async function(req, res, next){
    if(req.query.message!=undefined){
        microsoftTranslator.translate(req.query.message, res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize GET request. Maybe missing message parameter?\"}");
    }
});

router.post('/',function(req, res, next){
    if(req.body!=undefined){
        microsoftTranslator.translate(req.body.message, res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize POST request\"}");
    }
});

module.exports = router;