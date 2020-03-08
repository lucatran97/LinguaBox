var express = require('express');
var router = express.Router();

/*handle chat post request*/
router.get('/',function(req, res, next){
    if(req.query.message!=undefined){
    res.send("{\"status\": 200, \"message\"="+req.query.message+"}");
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