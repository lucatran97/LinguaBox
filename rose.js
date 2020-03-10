const dialogflow = require('dialogflow');
const LANGUAGE_CODE = 'en-US' 

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
			console.log(responses);
			return responses;
		}
		catch(err) {
			console.error('DialogFlow.sendTextMessageToDialogFlow ERROR:', err);
			throw err;
		}
	}
}

var df = new DialogFlow('sa1-lmtvhu');
df.sendTextMessageToDialogFlow('hello', '123456789');