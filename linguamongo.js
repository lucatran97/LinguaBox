const MongoClient = require('mongodb').MongoClient;

const uri = "mongodb+srv://trung_nguyen:linguabox@linguabox-no2v7.azure.mongodb.net/admin";
const client = new MongoClient(uri, { useNewUrlParser: true });

var dbCRUD = {
    createListing: async function (email, res){
        try {
            var newListing = {
                user_id: email
            }
            const result = await client.db("linguadb").collection("users").insertOne(newListing);
            res.send(JSON.stringify({status: "success", message: "Welcome new user " + email + "!"}));
            await client.close();
        } catch (err) {
            res.send(JSON.stringify({status: "failure", message: err.toString()}));
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
                    this.createListing(email, res);
                }
            } catch (err) {
                res.send(JSON.stringify({status: "failure", message: err.toString()}));
            }
        } else {
            res.send(JSON.stringify({status: "failure", message: "Invalid email"}));
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
