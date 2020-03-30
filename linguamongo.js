const MongoClient = require('mongodb').MongoClient;

const uri = "mongodb+srv://trung_nguyen:linguabox@linguabox-no2v7.azure.mongodb.net/admin";
const client = new MongoClient(uri, { useNewUrlParser: true });

var dict = {};
dict['es'] = "Spanish";
dict['de'] = "German";
dict['zh-Hans'] = "Chinese (Simplified)";
dict['vi'] = "Vietnamese";
dict['da'] = "Danish";
dict['nl'] = "Dutch";
dict['fr'] = "French";
dict['it'] = "Italian";
dict['ja'] = "Japanese";
dict['ru'] = "Russian";

var dbCRUD = {
    createListing: async function (newListing, collection, res){
        try {
            const result = await client.db("linguadb").collection(collection).insertOne(newListing);
            if (res!=null){
                res.send(JSON.stringify({status: "success", progress: []}));
            }
        } catch (err) {
            console.log(err.toString());
            if (res!=null){
                res.send(JSON.stringify({status: "failure", message: err.toString()}));
            }
        }
    },

    retrieveProgress: async function (email, res) {
        try {
            const progressArray = [];
            client.db("linguadb").collection("progress").find({ user_id: email }).toArray( function(err, result) {
                if (err) throw err;
                console.log(result);
                res.send(JSON.stringify({status: "success", progress: result}));
              });
        } catch (err) {
            console.log(err.toString());
            res.send(JSON.stringify({status: "failure", message: err.toString()}));
        }
    },

    signInHandler: async function (email, res) {
        if(validateEmail(email)){
            try{
                if(!isConnected()){
                    await client.connect();
                    console.log("Not connected. New connection now...");
                } else {
                    console.log("Already connected");
                }
                const result = await client.db("linguadb").collection("users")
                                    .findOne({ user_id: email });
            
                if (result) {
                    this.retrieveProgress(email, res);
                } else {
                    var newListing = {
                        user_id: email
                    }
                    this.createListing(newListing,"users", res);
                }
            } catch (err) {
                res.send(JSON.stringify({status: "failure", message: err.toString()}));
            }
        } else {
            res.send(JSON.stringify({status: "failure", message: "Invalid email"}));
        }
    },

    updateLanguageProgress: async function (email, req_language) {
        if(req_language != 'en'){
            var date_ob = new Date();
            var myQuery = { user_id: email, language: dict[req_language] };
            if(validateEmail(email)){
                try{
                    if(!isConnected()){
                        await client.connect();
                        console.log("Not connected. New connection now...");
                    } else {
                        console.log("Already connected");
                    }
                    var result = await client.db("linguadb").collection("progress")
                                        .findOne(myQuery);
                
                    if (result) {
                        var mess_no = result.messages_sent + 1;
                        var progress_no = result.progress;
                        var current_level = result.level;
                        var previousDate = result.last_session;
                        var c_streak = result.current_streak;
                        var l_streak = result.longest_streak;
                        if (mess_no % 10 == 0){
                            progress_no++;
                        }
                        var date_result = compareDate(previousDate, date_ob);
                        if (date_result==1){
                            progress_no+=10;
                            c_streak++;
                            if(c_streak>l_streak){
                                l_streak = c_streak;
                            }
                        } else if (date_result==-1) {
                            c_streak = 1;
                        }
                        if (progress_no>=100){
                            if (current_level < 3) {
                                progress_no = progress_no%100;
                                current_level++;
                            } else {
                                progress_no = 100;
                            }
                        }
                        var newValues = { $set: {
                            messages_sent: mess_no,
                            last_session: date_ob,
                            level: current_level,
                            progress: progress_no,
                            current_streak: c_streak,
                            longest_streak: l_streak
                        }};
                        console.log(newValues);
                        client.db("linguadb").collection("progress").updateOne(myQuery, newValues, function(err, res) {
                            if (err) throw err;
                            console.log("1 document updated");
                        });
                    } else {
                        let newListing = {
                            user_id: email,
                            language: dict[req_language],
                            last_session: date_ob,
                            level: 1,
                            progress: 0,
                            messages_sent: 1,
                            current_streak: 1,
                            longest_streak: 1
                        }
                        this.createListing(newListing, "progress", null);
                    }
                } catch (err) {
                    console.log(JSON.stringify({status: "failure", message: err.toString()}));
                }
            } else {
                console.log(JSON.stringify({status: "failure", message: "Invalid email"}));
            }
        }
    }
}

function validateEmail(email) {
    if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      return (true);
    }
      return (false);
}

function isConnected() {
    return !!client && !!client.topology && client.topology.isConnected()
}

function compareDate(previousDate, date_ob){
    if(previousDate.getTime()+86400000<date_ob.getTime()){ 
        console.log("Two dates are more than a day apart");
        return -1;
    } else if (previousDate.getDate()!=date_ob.getDate()){
        console.log("Two dates are consecutive");
        return 1;
    } else {
        console.log("Two dates are on the same day");
        return 0;
    }
}

module.exports.dbCRUD = dbCRUD;
