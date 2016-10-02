/**
 * FirebaseUI initialization to be used in a Single Page application context.
 */
// FirebaseUI config.
var uiConfig = {
  'callbacks': {
    // Called when the user has been successfully signed in.
    'signInSuccess': function(user, credential, redirectUrl) {
      handleSignedInUser(user);
      // Do not redirect.
      return false;
    }
  },
  // Opens IDP Providers sign-in flow in a popup.
  'signInFlow': 'popup',
  'signInOptions': [
    // TODO(developer): Remove the providers you don't need for your app.
    {
      provider: firebase.auth.GoogleAuthProvider.PROVIDER_ID,
      scopes: ['https://www.googleapis.com/auth/plus.login']
    },
    {
      provider: firebase.auth.FacebookAuthProvider.PROVIDER_ID,
      scopes :[
        'public_profile',
        'email',
        'user_likes',
        'user_friends'
      ]
    },
    firebase.auth.TwitterAuthProvider.PROVIDER_ID,
    firebase.auth.GithubAuthProvider.PROVIDER_ID,
    //firebase.auth.EmailAuthProvider.PROVIDER_ID
  ],
  // Terms of service url.
  'tosUrl': 'https://www.google.com'
};

// Initialize the FirebaseUI Widget using Firebase.
var ui = new firebaseui.auth.AuthUI(firebase.auth());
// Keep track of the currently signed in user.
var currentUid = null;

/**
 * Redirects to the FirebaseUI widget.
 */
var signInWithRedirect = function() {
  window.location.assign('/widget');
};


/**
 * Open a popup with the FirebaseUI widget.
 */
var signInWithPopup = function() {
  window.open('/widget', 'Sign In', 'width=985,height=735');
};

var updateInfo = function() {
  console.log(currentUid);
  console.log(document.getElementById('formGender').value);


  firebase.database().ref('users/' + currentUid).set({
    firstName: document.getElementById('formFirstName').value,
    lastName: document.getElementById('formLastName').value,
    email: document.getElementById('formEmail').value,
    gradDate: document.getElementById('formGradDate').value,
    major: document.getElementById('formMajor').value,
    school: document.getElementById('formSchool').value,
    gender: document.getElementById('formGender').value
  });
  document.getElementById('policyButton').removeAttribute('disabled');
}

var acceptPolicy = function(){

  alert("Please accept the privacy policy document sent to your email");
}
/**
 * Displays the UI for a signed in user.
 */
var handleSignedInUser = function(user) {
  currentUid = user.uid;
  document.getElementById('user-signed-in').style.display = 'block';
  document.getElementById('user-signed-out').style.display = 'none';
  document.getElementById('name').textContent = user.displayName;
  //document.getElementById('email').textContent = user.email;
  /*if (user.photoURL){
    document.getElementById('photo').src = user.photoURL;
    document.getElementById('photo').style.display = 'block';
  } else {
    document.getElementById('photo').style.display = 'none';
  }*/

  console.log("handling signed in user");

  firebase.database().ref('users/' + user.uid).once("value", function(snapshot) {
    var a = snapshot.exists();
    if (a == true) {
      console.log("User exists already");
      console.log(user.uid + " " + user.email + " " + snapshot.val().firstName);
      console.log(snapshot.val());
      document.getElementById('formEmail').setAttribute('value', snapshot.val().email);
      if (snapshot.val().firstName)
        document.getElementById('formFirstName').setAttribute('value', snapshot.val().firstName);
      else
        document.getElementById('formFirstName').removeAttribute('value');
      if (snapshot.val().lastName)
        document.getElementById('formLastName').setAttribute('value', snapshot.val().lastName);
      else
        document.getElementById('formLastName').removeAttribute('value');
      if (snapshot.val().major)
        document.getElementById('formMajor').setAttribute('value', snapshot.val().major);
      else
        document.getElementById('formMajor').removeAttribute('value');
      if (snapshot.val().gradDate)
        document.getElementById('formGradDate').setAttribute('value', snapshot.val().gradDate);
      else
        document.getElementById('formGradDate').removeAttribute('value');
      if (snapshot.val().school)
        document.getElementById('formSchool').setAttribute('value', snapshot.val().school);
      else
        document.getElementById('formSchool').removeAttribute('value');
      console.log(snapshot.val().gender);
      if (snapshot.val().gender)
        document.getElementById('activeGender').setAttribute('value', snapshot.val().gender);
      else
        document.getElementById('formGender').removeAttribute('value');

    }
    else {
      console.log("User does not exist");
      firebase.database().ref('users/' + user.uid).set({
        firstName: '',
        lastName: '',
        email: user.email,
        ResumeLink: 'urltest'
      });
    };
  });

  var auth = firebase.auth();
    var storageRef = firebase.storage().ref();
  //document.getElementById('file').addEventListener('change', handleFileSelect, false);
  document.getElementById('file').addEventListener('change', function(evt) {
    console.log("HELLO");
      evt.stopPropagation();
      evt.preventDefault();
      var file = evt.target.files[0];
      var metadata = {
        'contentType': file.type
      };
      // Push to child path.
      // [START oncomplete]
      storageRef.child(file.name).put(file, metadata).then(function(snapshot) {
        console.log('Uploaded', snapshot.totalBytes, 'bytes.');
        console.log(snapshot.metadata);
        var url = snapshot.metadata.downloadURLs[0];
        console.log('File available at', url);
        firebase.database().ref('users/' + user.uid).update({
          ResumeLink: url,
        });
        // [START_EXCLUDE]
        document.getElementById('linkbox').innerHTML = '<a href="' +  url + '">Click For File</a>';
        // [END_EXCLUDE]
      }).catch(function(error) {
        // [START onfailure]
        console.error('Upload failed:', error);
        // [END onfailure]
      });
      // [END oncomplete]
  }, false);

};

/**
 * Displays the UI for a signed out user.
 */
var handleSignedOutUser = function() {
  document.getElementById('user-signed-in').style.display = 'none';
  document.getElementById('user-signed-out').style.display = 'block';
  ui.start('#firebaseui-container', uiConfig);
};

// Listen to change in auth state so it displays the correct UI for when
// the user is signed in or not.
firebase.auth().onAuthStateChanged(function(user) {
  // The observer is also triggered when the user's token has expired and is
  // automatically refreshed. In that case, the user hasn't changed so we should
  // not update the UI.
  if (user && user.uid == currentUid) {
    return;
  }
  document.getElementById('loading').style.display = 'none';
  document.getElementById('loaded').style.display = 'block';
  user ? handleSignedInUser(user) : handleSignedOutUser();
});


/**
 * Initializes the app.
 */
var initApp = function() {
  /*document.getElementById('sign-in-with-redirect').addEventListener(
      'click', signInWithRedirect);
  document.getElementById('sign-in-with-popup').addEventListener(
      'click', signInWithPopup);*/
  document.getElementById('sign-out').addEventListener('click', function() {
    firebase.auth().signOut();
  });
  /*document.getElementById('delete-account').addEventListener(
      'click', function() {
        firebase.auth().currentUser.delete();
      });*/
};

window.addEventListener('load', initApp);
