const request = require('request');
const uuidv4 = require('uuid/v4');
const rose = require('./rose');

// Getting Microsoft subscription key
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

/**
 * Translation function
 * @param {*} message 
 * @param {*} opts 
 * @param {*} sRes 
 */
var translate = async function(message, opts, sRes){
  if(message.length >= 256){
    sRes.send(JSON.stringify({status: "failure", message: 'Text message should not be more than 256 characters.'}));
  } else {
    var language_to, language_from;
    if(opts.stage==="NORM"){
      language_to = opts.language_to;
      language_from = opts.language_from;
    } else if (opts.stage==="PRE") {
      language_from = opts.language?opts.language:'es';
      language_to = 'en';
    } else {
      language_to = opts.language?opts.language:'es';
      language_from = 'en';
    }
    //console.log(language_from);
    //console.log(language_to);
    if (language_to===language_from&&language_from==='en'){
      if (opts.stage==="NORM"){
        sRes.send(JSON.stringify({status: "success", translation: message}));
      } else if (opts.stage==="PRE"){
        rose.inputHandler.onPreTransateSuccess(message, opts.session, opts.language, sRes);
      } else {
        rose.inputHandler.onPostTranslateSuccess(message, message, sRes);
      }
    } else {
      let options = {
        method: 'POST',
        baseUrl: endpoint,
        url: 'translate',
        qs: {
          'api-version': '3.0',
          'from': language_from,
          'to': [language_to]
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
          sRes.send(JSON.stringify({status: "failure", message: "Cannot evoke Microsoft Translator."}));
          console.log("Cannot evoke Microsoft API at stage: " + opts.stage);
        } else {
          if(opts.stage==="PRE"){
            rose.inputHandler.onPreTransateSuccess(body[0].translations[0].text.replace(/["]+/g, ''), opts.session, opts.language, sRes);
          } else if (opts.stage==="POST") {
            rose.inputHandler.onPostTranslateSuccess(body[0].translations[0].text.replace(/["]+/g, ''), message, sRes);
          } else {
            sRes.send(JSON.stringify({status: "success", translation: body[0].translations[0].text.replace(/["]+/g, '')}));
          }
        }
      });
    }
  }
}

module.exports.translate = translate;