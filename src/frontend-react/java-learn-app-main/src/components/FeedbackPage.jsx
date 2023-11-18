import React from "react";
import FeedbacksTable from "./FeedbacksTable";
import { Link, Route, Switch } from "react-router-dom";
import { withRouter } from "react-router";
import TicketService from '../services/TicketService';
import FeedbackService from '../services/FeedbackService';

import {
  Button,
  Typography,
  TextField,
  FormControl
} from "@material-ui/core";
import MainPageWithRouter from "./MainPage";

class FeedbackPage extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      ticketId: this.props.match.params.ticketId,
      ticketName: '',
      rate: '',
      text: '',
      ticketFeedbacks: [],
      accessFeedbackError: '',
      isAccess: true,
      createFeedbackError: [],
      isInvalidFeedback: false
    }

    this.handleRateChange = this.handleRateChange.bind(this);
    this.handleTextChange = this.handleTextChange.bind(this);
    this.handleSubmitFeedback = this.handleSubmitFeedback.bind(this);
  }

  componentDidMount() {
    const ticketId = this.state.ticketId;

    TicketService.getTicketById(ticketId).then((res) => {
      this.setState({ ticketName: res.data.name });
    });

    FeedbackService.getLastFiveFeedbacksByTicketId(ticketId).then(res => {
      this.setState({ ticketFeedbacks: res.data });
    });
  }

  handleRateChange = (event) => {
    this.setState({
      rate: event,
    });
  };

    handleTextChange = (event) => {
      this.setState({
        text: event.target.value,
      });
    };

    handleSubmitFeedback = () => {
      const ticketId = this.state.ticketId;

      const feedback = {
        rate: this.state.rate,
        text: this.state.text
      }

      FeedbackService.createFeedback(feedback, ticketId).then(res => {
        this.setState({
          ticketFeedbacks: [...this.state.ticketFeedbacks, feedback],
        });

        this.props.history.push('/tickets');
      }).catch(err => {
        if (err.response.status === 403) {
          this.setState({ haveAccess: false });
          this.setState({ accessFeedbackError: err.response.data })
          this.setState({ isInvalidFeedback: false });
        }
        if (err.response.status === 400) {
          this.setState({ isInvalidFeedback: true });
          this.setState({ createFeedbackError: err.response.data })
        }
      })
    }

    handleShowAllFeedbacks = (event) => {
      FeedbackService.getAllFeedbacksByTicketId(this.state.ticketId).then((res) => {
        this.setState({ ticketFeedbacks: res.data })
      });
    }

    render() {
      const { ticketId, ticketName, ticketFeedbacks, createFeedbackError, accessFeedbackError,
              haveAccess, isInvalidFeedback } = this.state;
      const { url } = this.props.match;
      const { handleShowAllFeedbacks, handleRateChange, handleTextChange, handleSubmitFeedback } = this;

       return (
         <Switch>
           <Route exact path={url}>
             <div className="ticket-data-container__title">
               <div className="submit-button-section">
                 <Button
                   component={Link} to="/tickets"
                   variant="contained"
                   color="primary"
                 >
                   Ticket List
                 </Button>
                 <Button
                   component={Link} to={`/tickets/${ticketId}`}
                   variant="contained"
                   color="secondary"
                 >
                   Ticket overview
                 </Button>
               </div>
               <Typography variant="h4">
                 {`Ticket №${ticketId} - ${ticketName}`}
               </Typography>
               <Typography variant="h5">
                 Add Feedback
               </Typography>
               {!haveAccess &&
                 <Typography className="has-error" component="h6" variant="h5" align='center'>
                   {accessFeedbackError.info}
                 </Typography>
               }
               {isInvalidFeedback &&
                 <Typography className="has-error" component="h6" variant="h5" align='center'>
                   {createFeedbackError.map((error) => (
                     <div>
                       {error.rate}
                     </div>
                   ))}
                 </Typography>
               }
               <FormControl>
                 <div className="feedback-rate">
                   <button onClick={() => handleRateChange("terrible")}>Terrible</button>
                   <button onClick={() => handleRateChange("bad")}>Bad</button>
                   <button onClick={() => handleRateChange("medium")}>Medium</button>
                   <button onClick={() => handleRateChange("good")}>Good</button>
                   <button onClick={() => handleRateChange("great")}>Great</button>
                 </div>
                 <TextField
                   label="Comment"
                   multiline
                   rows={4}
                   variant="outlined"
                   className="creation-text-field creation-text-field_width680"
                   onChange={handleTextChange}
                 />
                 <div className="ticket-creation-form-container__navigation-container" align='right'>
                   <Button
                     onClick={() => handleSubmitFeedback()}
                     variant="contained"
                     color="secondary"
                   >
                     Submit
                   </Button>
                 </div>
               </FormControl>
               <Typography align="left" variant="h5">
                 Feedbacks:
               </Typography>
               <FeedbacksTable
                 feedbacks={ticketFeedbacks}
                 showAllFeedbacksCallback={handleShowAllFeedbacks}
               />
             </div>
           </Route>
           <Route path="/tickets">
             <MainPageWithRouter />
           </Route>
       </Switch>
    );
  }
}

const FeedbackPageWithRouter = withRouter(FeedbackPage);
export default FeedbackPageWithRouter;