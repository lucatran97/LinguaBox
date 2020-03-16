const dialogflow = require('dialogflow');
const LANGUAGE_CODE = 'en-US'
var microsoftTranslator = require('./microsoft');

class DialogFlow {
	constructor (projectId) {
		this.projectId = projectId;

		let privateKey = process.env.DIALOGFLOW_PRIVATE_KEY.replace(/\\n/gm, '\n');
		let clientEmail = process.env.DIALOGFLOW_CLIENT_EMAIL;
		let config = {
			credentials: {
				private_key: privateKey,
				client_email: clientEmail
			}
		};
	
		this.sessionClient = new dialogflow.SessionsClient(config);
	}

	async chatbotQuery(textMessage, sessionId, language, sRes){
		let responses = await this.sendTextMessageToDialogFlow(decodeURI(textMessage), sessionId);
		if((responses!=undefined)&&(responses[0].queryResult.fulfillmentText!=undefined)){
			//console.log(responses);
			inputHandler.onChatbotSuccess(responses[0].queryResult.fulfillmentText, sessionId, language, sRes);
		} else {
			sRes.send(JSON.stringify({message: 'Problem with LinguaBox server and/or chatbot connection.'})); 
		}
	}

	async sendTextMessageToDialogFlow(textMessage, sessionId) {
		// Define session path
		//console.log(this.projectId);
		const sessionPath = this.sessionClient.sessionPath(this.projectId, sessionId);
		// The text query request.
		const request = {
			session: sessionPath,
			queryInput: {
				text: {
					text: textMessage,
					languageCode: LANGUAGE_CODE
				}
			}
		};
		try {
			let responses = await this.sessionClient.detectIntent(request);		
			console.log('DialogFlow.sendTextMessageToDialogFlow: Detected intent');
			//console.log(responses);
			return responses;
		}
		catch(err) {
			console.error('DialogFlow.sendTextMessageToDialogFlow ERROR:', err);
			throw err;
		}
	}
}

var df = new DialogFlow('sa1-lmtvhu');

var inputHandler = {
	processChat: async function(message, sessionId, language, res){
		microsoftTranslator.translate(message, "PRE", sessionId, language, res);
	},
	onPreTransateSuccess: async function(message, sessionId, language, res){
		df.chatbotQuery(message, sessionId, language, res);
	},
	onChatbotSuccess: async function(message, sessionId, language, res){
		microsoftTranslator.translate(message, "POST", sessionId, language, res);
	},
	onPostTranslateSuccess: async function(rMessage, rTranslation, res){
		res.send(JSON.stringify({message: rMessage, translation: rTranslation}));
	}
}
module.exports.inputHandler = inputHandler;