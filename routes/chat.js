var express = require('express');
var router = express.Router();
const rose = require('../rose');

/*handle chat post request*/
router.get('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if(req.query.message!=undefined){
        rose.processChat(encodeURI(req.query.message), '123456789', res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize GET request. Maybe missing message parameter?\"}");
    }
});

router.post('/', async function(req, res, next){
    res.setHeader('Content-Type', 'application/json');
    if((req.body!=undefined)&&(req.body.message!=undefined)){
        rose.chatbotQuery(encodeURI(req.body.message), '123456789', res);
    } else {
        res.send("{\"status\": 404, \"message\" = \"Cannot recognize POST request\"}");
    }
});

module.exports = router;