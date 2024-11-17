import { initializeApp } from 'firebase/app';
import {getAuth, GoogleAuthProvider, signInWithPopup, signOut, onAuthStateChanged } from 'firebase/auth';
const firebaseConfig = {
    apiKey: "AIzaSyAq5YJOwXzRQkKjfxZwAYZRSQmNrgsHTw0",
    authDomain: "test-d24e2.firebaseapp.com",
    projectId: "test-d24e2",
    storageBucket: "test-d24e2.appspot.com",
    messagingSenderId: "117583291248",
    appId: "1:117583291248:web:7627b98766ee1193c8a5b5",
    measurementId: "G-44F6LEEM1S"
};
const app = initializeApp(firebaseConfig);

// Initialize Firebase Authentication and Google Auth Provider
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { auth, googleProvider, signInWithPopup , signOut, onAuthStateChanged };