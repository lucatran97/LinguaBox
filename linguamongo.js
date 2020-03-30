const MongoClient = require('mongodb').MongoClient;

const uri = "mongodb+srv://trung_nguyen:linguabox@linguabox-no2v7.azure.mongodb.net/admin";
const client = new MongoClient(uri, { useNewUrlParser: true });

var dict = {};
dict['es'] = "Spanish";
dict['de'] = "German";
dict['zh-Hans'] = "Chinese (Simplified)";

var dbCRUD = {
    createListing: async function (newListing, res){
        try {
            const result = await client.db("linguadb").collection("users").insertOne(newListing);
            console.log("res");
            if (res!=null){
                res.send(JSON.stringify({status: "success", message: "Welcome new user " + email + "!"}));
            }
            await client.close();
        } catch (err) {
            console.log(err.toString());
            if (res!=null){
                res.send(JSON.stringify({status: "failure", message: err.toString()}));
            }
        }
    },

    signInHandler: async function (email, res) {
        if(validateEmail(email)){
            try{
                await client.connect();
                result = await client.db("linguadb").collection("users")
                                    .findOne({ user_id: email });
            
                if (result) {
                    res.send(JSON.stringify({status: "success", message: "Welcome back " + email + "!"}));
                } else {
                    var newListing = {
                        user_id: email
                    }
                    this.createListing(newListing, res);
                }
            } catch (err) {
                res.send(JSON.stringify({status: "failure", message: err.toString()}));
            }
        } else {
            res.send(JSON.stringify({status: "failure", message: "Invalid email"}));
        }
    },

    checkLanguageProgress: async function (email, req_language) {
        var date_ob = new Date();
        var myQuery = { user_id: email };
        if(validateEmail(email)){
            try{
                await client.connect();
                result = await client.db("linguadb").collection("progress")
                                    .findOne(myQuery);
            
                if (result) {
                    var newValues = { $set: {messages_sent: result.messages_sent + 1, last_session: date_ob} };
                    client.db("linguadb").collection("progress").updateOne(myQuery, newValues, function(err, res) {
                        if (err) throw err;
                        console.log("1 document updated");
                      });
                      await client.close();
                } else {
                    let newListing = {
                        user_id: email,
                        language: dict[req_language],
                        last_session: date_ob,
                        level: 1,
                        progress: 0,
                        mesages_sent: 0,
                        current_streak: 1,
                        longest_streak: 1
                    }
                    this.createListing(newListing, null);
                }
            } catch (err) {
                console.log(JSON.stringify({status: "failure", message: err.toString()}));
                await client.close();
            }
        } else {
            console.log(JSON.stringify({status: "failure", message: "Invalid email"}));
        }
    }
}

function validateEmail(email) {
    if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      return (true);
    }
      return (false);
}

module.exports.dbCRUD = dbCRUD;
