const MongoClient = require('mongodb').MongoClient;

const uri = "mongodb+srv://trung_nguyen:linguabox@linguabox-no2v7.azure.mongodb.net/admin";
const client = new MongoClient(uri, { useNewUrlParser: true });

var dbCRUD = {
    listDatabases: async function (){
        try {
            await client.connect();
            databasesList = await client.db().admin().listDatabases();
        
            console.log("Databases:");
            databasesList.databases.forEach(db => console.log(` - ${db.name}`));
            await client.close();
        } catch (err) {
            console.log(err);
        }
    },
    createListing: async function (newListing){
        try {
            await client.connect();
            const result = await client.db("sample_airbnb").collection("listingsAndReviews").insertOne(newListing);
            console.log(`New listing created with the following id: ${result.insertedId}`);
            await client.close();
        } catch (err) {
            console.log(err);
        }
    },
    findListingByName: async function (nameOfListing) {
        try{
            await client.connect();
            result = await client.db("sample_airbnb").collection("listingsAndReviews")
                                .findOne({ name: nameOfListing });
        
            if (result) {
                console.log(`Found a listing in the collection with the name '${nameOfListing}':`);
                console.log(result);
            } else {
                console.log(`No listings found with the name '${nameOfListing}'`);
            }
            await client.close();
        } catch (err) {
            console.log(err);
        }
    },
    updateListingByName: async function (nameOfListing, updatedListing) {
        try {
            await client.connect();
            result = await client.db("sample_airbnb").collection("listingsAndReviews")
                                .updateOne({ name: nameOfListing }, { $set: updatedListing });
        
            console.log(`${result.matchedCount} document(s) matched the query criteria.`);
            console.log(`${result.modifiedCount} document(s) was/were updated.`);
            await client.close();
        } catch (err) {
            console.log(err);
        }
    },
    deleteListingByName: async function(nameOfListing) {
        try {
            await client.connect();
            result = await client.db("sample_airbnb").collection("listingsAndReviews")
                    .deleteOne({ name: nameOfListing });
            console.log(`${result.deletedCount} document(s) was/were deleted.`);
            await client.close();
        } catch (err) {
            console.log(err);
        }
    }
}

module.exports.dbCRUD = dbCRUD;
