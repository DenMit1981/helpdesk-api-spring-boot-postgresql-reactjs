import "./App.css";
import AuthenticatedRoute from './components/AuthenticatedRoute';
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import MainPageWithRouter from "./components/MainPage";
import TicketCreationPageWithRouter from "./components/TicketCreationPage";
import TicketEditPageWithRouter from "./components/TicketEditPage";
import TicketInfo from "./components/TicketInfo";
import FeedbackPage from "./components/FeedbackPage";

import {
  BrowserRouter as Router,
  Route,
  Switch,
} from "react-router-dom";

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/" exact component={LoginPage} />
        <Route path="/register" exact component={RegisterPage} />
        <AuthenticatedRoute path="/tickets" exact component={MainPageWithRouter} />
        <Route path="/add-ticket" component={TicketCreationPageWithRouter} />
        <Route path="/tickets/:id" component={TicketInfo} />
        <Route path="/update-ticket/:id" component={TicketEditPageWithRouter} />
        <Route path="/feedbacks/:ticketId" component={FeedbackPage} />
      </Switch>
    </Router>
  );
}

export default App;
