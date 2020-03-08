var express = require('express');
var router = express.Router();
var microsoftTranslator = require('../microsoft');

/*handle chat post request*/
router.get('/',async function(req, res, next){
    if(req.query.message!=undefined){
        microsoftTranslator.translate(req.query.message, res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize GET request\"}");
    }
});

router.post('/',function(req, res, next){
    if(req.body!=undefined){
        res.send(req.body.message);
        //res.send(prepare(microsoftTranslator.translate(req.body.message)));
    } else if (req.query.message!=undefined){
        res.send(req.query.message);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize POST request\"}");
    }
});

module.exports = router;