const dialogflow = require('dialogflow');
const LANGUAGE_CODE = 'en-US'
var microsoftTranslator = require('./microsoft');
var linguamongo = require('./linguamongo');

class DialogFlow {
	constructor (level) {
		this.projectId = 'sa1-lmtvhu';
		var privateKey = process.env.DIALOGFLOW_PRIVATE_KEY.replace(/\\n/gm, '\n');
		var clientEmail = process.env.DIALOGFLOW_CLIENT_EMAIL;
		switch (level) {
			case 1:
				break;
			case 2:
				this.projectId = 'lingua-intermediate-olokkp';
				privateKey = process.env.DIALOGFLOW_PRIVATE_KEY_INT.replace(/\\n/gm, '\n');
				clientEmail = process.env.DIALOGFLOW_CLIENT_EMAIL_INT;
				break;
			case 3:
				this.projectId = 'lingua-hard-qtisyo';
				privateKey = process.env.DIALOGFLOW_PRIVATE_KEY_HARD.replace(/\\n/gm, '\n');
				clientEmail = process.env.DIALOGFLOW_CLIENT_EMAIL_HARD;
				break;
		}
		//Base64.encodeBase64URLSafeString
		let config = {
			credentials: {
				private_key: privateKey,
				client_email: clientEmail
			}
		};
	
		this.sessionClient = new dialogflow.SessionsClient(config);
	}

	async chatbotQuery(textMessage, sessionId, language, sRes){
		if(textMessage.length >= 256){
			sRes.send(JSON.stringify({status: "failure", message: 'Text message should not be more than 256 characters.'}));
		} else {
			let responses = await this.sendTextMessageToDialogFlow(decodeURI(textMessage), sessionId);
			if((responses!=undefined)&&(responses[0].queryResult.fulfillmentText!=undefined)){
				//console.log(responses);
				inputHandler.onChatbotSuccess(responses[0].queryResult.fulfillmentText, sessionId, language, sRes);
			} else {
				sRes.send(JSON.stringify({status: "failure", message: 'Problem with LinguaBox server and/or chatbot connection.'})); 
			}
		}
	}

	async sendTextMessageToDialogFlow(textMessage, sessionId) {
		// Define session path
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

var inputHandler = {
	processChat: async function(message, sessionId, language, res){
		var opts = {stage: "PRE", session: sessionId, language: language};
		microsoftTranslator.translate(message, opts, res);
	},
	onPreTransateSuccess: async function(message, sessionId, language, res){
		linguamongo.dbCRUD.updateLanguageProgress(message, sessionId, language, res);
	},
	onQuerySuccess: async function (message, sessionId, language, res, level){
		var df = new DialogFlow(level);
		df.chatbotQuery(message, sessionId, language, res);
	},
	onChatbotSuccess: async function(message, sessionId, language, res){
		var opts = {stage: "POST", session: sessionId, language: language};
		microsoftTranslator.translate(message, opts, res);
	},
	onPostTranslateSuccess: async function(rMessage, rTranslation, res){
		res.send(JSON.stringify({message: rMessage, translation: rTranslation}));
	}
}
module.exports.inputHandler = inputHandler;