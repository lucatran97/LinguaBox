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

	async chatbotQuery(textMessage, sessionId, sRes){
		let responses = await this.sendTextMessageToDialogFlow(decodeURI(textMessage), sessionId);
		if((responses!=undefined)&&(responses[0].queryResult.fulfillmentText!=undefined)){
			console.log(responses);
			//sRes.send(JSON.stringify({status:200, message: responses[0].queryResult.fulfillmentText}));
		} else {
			//sRes.send(JSON.stringify({status:500, message: 'Problem with LinguaBox server and/or chatbot connection'})); 
		}
	}

	async sendTextMessageToDialogFlow(textMessage, sessionId) {
		// Define session path
		console.log(this.projectId);
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
			console.log(responses);
			return responses;
		}
		catch(err) {
			console.error('DialogFlow.sendTextMessageToDialogFlow ERROR:', err);
			throw err;
		}
	}
}

var inputHandler = {
 	df: new DialogFlow('sa1-lmtvhu'),
	processChat: async function(message, sessionId, res){
		microsoftTranslator.translate(message, sessionId, res);
	},
	onPreTransateSuccess: async function(message, sessionId, res){
		df.chatbotQuery(message, sessionId, res);
	},
	onChatbotSuccess: async function(message, sessionId, res){
		microsoftTranslator.translate(message, sessionId, res);
	},
	onPostTranslateSuccess: async function(message, sessionId, res){
		res.
	}
}
module.exports.processChat = inputHandler.processChat;