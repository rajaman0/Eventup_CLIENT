var request = require('request');
var express = require('express');
var fs = require('fs');
var app = express();
var async = require("async");	// async module
var request = require("request");	// request module
var fs = require("fs");			// fs module

var email = "alan.friday@gmail.com";			// your account email
var password = "password";			// your account password
var integratorKey = "846c9053-98cd-4e7f-a2f8-c758cbe0a127";		// your Integrator Key (found on the Preferences -> API page)
var recipientName = "Alan Thomas";			// recipient (signer) name
var documentName = "EventumPolicy.pdf";		// copy document with this name into same directory!
var baseUrl = ""; 				// we will retrieve this through the Login call
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

app.set('port', (process.env.PORT || 5000));
// parse application/json
app.use(bodyParser.json());

app.use(express.static(__dirname));

app.get('/', function(req,res){
	res.sendFile("./index.html");
});


app.post('/',function(req,responseString){
	// Request Signature on a Document (Node.js)

// To run this sample
//  1. Copy the file to your local machine and give .js extension (i.e. example.js)
//  2. Change "***" to appropriate values
//  3. Install async and request packages
//     npm install async
//     npm install request
//     npm install fs
//  4. execute
//     node example.js
//

async.waterfall(
  [
    /////////////////////////////////////////////////////////////////////////////////////
    // Step 1: Login (used to retrieve your accountId and baseUrl)
    /////////////////////////////////////////////////////////////////////////////////////
    function(next) {
		var url = "https://demo.docusign.net/restapi/v2/login_information";
		var body = "";	// no request body for login api call

		// set request url, method, body, and headers
		var options = initializeRequest(url, "GET", body, email, password);

		// send the request...
		request(options, function(err, res, body) {
			if(!parseResponseBody(err, res, body)) {
				return;
			}
			baseUrl = JSON.parse(body).loginAccounts[0].baseUrl;
			next(null); // call next function
		});
	},

    /////////////////////////////////////////////////////////////////////////////////////
    // Step 2: Request Signature on a PDF Document
    /////////////////////////////////////////////////////////////////////////////////////
    function(next) {
    	var url = baseUrl + "/envelopes";
    	// following request body will place 1 signature tab 100 pixels to the right and
    	// 100 pixels down from the top left of the document that you send in the request
		var body = {
			"recipients": {
				"signers": [{
					"email":req.body.user.email,
					"name": req.body.user.firstName + " " + req.body.user.lastName,
					"recipientId": 1,
					"tabs": {
						"signHereTabs": [{
							"xPosition": "100",
							"yPosition": "100",
							"documentId": "1",
							"pageNumber": "1"
						}]
					}
				}]
			},
			"emailSubject": 'DocuSign API - Signature Request on Document Call',
			"documents": [{
				"name": documentName,
				"documentId": 1,
			}],
			"status": "sent",
		};

		// set request url, method, body, and headers
		var options = initializeRequest(url, "POST", body, email, password);

		// change default Content-Type header from "application/json" to "multipart/form-data"
		options.headers["Content-Type"] = "multipart/form-data";

		// configure a multipart http request with JSON body and document bytes
		options.multipart = [{
					"Content-Type": "application/json",
					"Content-Disposition": "form-data",
					"body": JSON.stringify(body),
				}, {
					"Content-Type": "application/pdf",
					'Content-Disposition': 'file; filename="' + documentName + '"; documentId=1',
					"body": fs.readFileSync(documentName),
				}
		];

		// send the request...
		request(options, function(err, res, body) {
			parseResponseBody(err, res, body);
		});
	} // end function
]);


//***********************************************************************************************
// --- HELPER FUNCTIONS ---
//***********************************************************************************************
function initializeRequest(url, method, body, email, password) {
	var options = {
		"method": method,
		"uri": url,
		"body": body,
		"headers": {}
	};
	addRequestHeaders(options, email, password);
	return options;
}

///////////////////////////////////////////////////////////////////////////////////////////////
function addRequestHeaders(options, email, password) {
	// JSON formatted authentication header (XML format allowed as well)
	dsAuthHeader = JSON.stringify({
		"Username": email,
		"Password": password,
		"IntegratorKey": integratorKey	// global
	});
	// DocuSign authorization header
	options.headers["X-DocuSign-Authentication"] = dsAuthHeader;
}

///////////////////////////////////////////////////////////////////////////////////////////////
function parseResponseBody(err, res, body) {
	console.log("\r\nAPI Call Result: \r\n", JSON.parse(body));
	responseString.end()
	if( res.statusCode != 200 && res.statusCode != 201)	{ // success statuses
		console.log("Error calling webservice, status is: ", res.statusCode);
		console.log("\r\n", err);
		return false;
	}
	return true;
}
});


var server = app.listen(process.env.PORT || 3000, function(){
  console.log("Express server listening on port %d in %s mode", this.address().port, app.settings.env);
});
