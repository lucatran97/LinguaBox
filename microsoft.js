const request = require('request');
const uuidv4 = require('uuid/v4');
const rose = require('./rose');

var key_var = 'TRANSLATOR_TEXT_SUBSCRIPTION_KEY';
if (!process.env[key_var]) {
    throw new Error('Please set/export the following environment variable: ' + key_var);
}
var subscriptionKey = process.env[key_var];
var endpoint_var = 'TRANSLATOR_TEXT_ENDPOINT';
if (!process.env[endpoint_var]) {
    throw new Error('Please set/export the following environment variable: ' + endpoint_var);
}
var endpoint = process.env[endpoint_var];

var learning;
var translate = async function(message, stage, sessionID, language, sRes){
  learning = language?language:'es';
  console.log(learning);
  var base = (stage==="PRE")?learning:'en';
  var target = (stage==="PRE")?'en':learning;
  let options = {
    method: 'POST',
    baseUrl: endpoint,
    url: 'translate',
    qs: {
      'api-version': '3.0',
      'from': base,
      'to': [target]
    },
    headers: {
      'Ocp-Apim-Subscription-Key': subscriptionKey,
      'Content-type': 'application/json',
      'X-ClientTraceId': uuidv4().toString()
    },
    body: [{
          'text': decodeURI(message)
    }],
    json: true,
};
  request(options, function(err, res, body){
    if(!body[0].translations){
      sRes.send(JSON.stringify({message: "Cannot evoke Microsoft Translator."}));
      console.log("Cannot evoke Microsoft API at stage: " + stage);
    } else {
      if(stage==="PRE"){
        rose.inputHandler.onPreTransateSuccess(body[0].translations[0].text.replace(/["]+/g, ''), sessionID, language, sRes);
      } else {
        rose.inputHandler.onPostTranslateSuccess(body[0].translations[0].text.replace(/["]+/g, ''), message, sRes);
      }
    }
  });
}

module.exports.translate = translate;