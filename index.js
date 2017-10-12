const functions = require('firebase-functions');
var gcs = require('@google-cloud/storage')();

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// var serviceAccount = require("D:\Workspace\LightApp\LightApp-824e6e349cd4.json");
// admin.initializeApp({
    // credential: admin.credential.cert(serviceAccount),
//     databaseURL: "https://lightapp-d3dc5.firebaseio.com"
// });
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.doSentimentAnalysis = functions.https.onRequest((request, response) => {
    const userUid = request.query.userUid;

    var messagesDatabase = 'users/' + userUid + '/messages';
    var positive = 0;
    var neutral = 0;
    var negative = 0;
    var notClassified = 0;
    var found = false;

    var teste = {
        feliz: 1,
        alegre: 1,
        batata: 0,
        cachorro: 0,
        triste: -1,
        infeliz: -1
    };

    var myBucket = gcs.bucket('sentiment_analysis');
    var myFile = myBucket.file('teste.txt');
    // myFile.acl.readers.addAllUsers();

    // var options = {
    //     entity: 'allUsers',
    //     role: gcs.acl.READER_ROLE
    // };

    // myBucket.acl.add(options, function(err, aclObject) {});
    //
    // myBucket.makePublic(function(err) {});

    myFile.download().then(function(data) {
        if (data) {
            response.send(data.toString('utf-8'));
        }
    });



    return admin.database().ref(messagesDatabase).once('value', (snapshot) => {
       var messages = snapshot.val();

       //loop in the messages of the user
        for (var key in messages){
            var words = messages[key].split(" ");
            //loop in the words present in the message
            for (var i=0; i<words.length; i++){
                //eliminates punctuation
                var word = words[i].replace(/[^\w\s]/gi, "");
                // console.log("Words: " + word);
                //loop in the words database
                for (var key2 in teste){
                    if (key2 == word){
                        found = true;
                        //classify word
                        if (teste[key2] == 1){
                            positive++;
                        } else if (teste[key2] == -1){
                            negative++;
                        } else if (teste[key2] == 0) {
                            neutral++;
                        }
                        break;
                    } else {
                        found = false;
                    }
                }
                //word not found in the lexico
                if (!found){
                    notClassified++;
                }
            }
        }

       console.log("Positive: " + positive );
       console.log("Negative: " + negative );
       console.log("Neutral: " + neutral );
       console.log("Not Classified: " + notClassified );

       var result = {
           put : function (key, value){
               this[key] = value
           },
           get : function (key) {
               return this[key]
           }
       };

       result.put("Positive", positive);
       result.put("Negative", negative);
       result.put("Neutral", neutral);
       result.put("Not Classified", notClassified);
       // response.send(result);
    });

});
