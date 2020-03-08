const request = require('request');
const uuidv4 = require('uuid/v4');

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

var translate = async function(message, sRes){
  let options = {
    method: 'POST',
    baseUrl: endpoint,
    url: 'translate',
    qs: {
      'api-version': '3.0',
      'from': 'es',
      'to': ['en']
    },
    headers: {
      'Ocp-Apim-Subscription-Key': subscriptionKey,
      'Content-type': 'application/json',
      'X-ClientTraceId': uuidv4().toString()
    },
    body: [{
          'text': message
    }],
    json: true,
};
  var result; 
  request(options, function(err, res, body){
    if(!body[0].translations){
      sRes.send("{\"status\": 500, \"message\" = \"Problem with Linguabox server and/or Microsoft Translation server\"}");  
    } else {
      sRes.send("{\"status\": 200, \"message\" = "+body[0].translations[0].text+"}");
    }
  });
}

module.exports.translate = translate;