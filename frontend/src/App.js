import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import './App.css';
import Navbar from './components/layout/Navbar';
// Import your page components
// import Home from './pages/Home/Home';
// import Browse from './pages/Browse/Browse';

// Placeholder components until you create the actual ones
const Home = () => <div><h1>Home Page</h1><p>Welcome to Tunelar</p></div>;
const Browse = () => <div><h1>Browse Page</h1><p>Browse samples here</p></div>;
const Upload = () => <div><h1>Upload Page</h1><p>Upload your samples here</p></div>;
const Profile = () => <div><h1>Profile Page</h1><p>User profile information</p></div>;

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <main className="container">
          <Switch>
            <Route exact path="/" component={Home} />
            <Route path="/browse" component={Browse} />
            <Route path="/upload" component={Upload} />
            <Route path="/profile" component={Profile} />
          </Switch>
        </main>
      </div>
    </Router>
  );
}

export default App;