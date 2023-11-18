import React from "react";
import PropTypes from "prop-types";
import CommentsTable from "./CommentsTable";
import HistoryTable from "./HistoryTable";
import TabPanel from "./TabPanel";
import { Link, Route, Switch } from "react-router-dom";
import { withRouter } from "react-router";
import TicketService from '../services/TicketService';
import HistoryService from '../services/HistoryService';
import CommentService from '../services/CommentService';
import AttachmentService from '../services/AttachmentService';

import {
  Button,
  ButtonGroup,
  Paper,
  Tab,
  Tabs,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Typography,
  TextField,
} from "@material-ui/core";

function a11yProps(index) {
  return {
    id: `full-width-tab-${index}`,
    "aria-controls": `full-width-tabpanel-${index}`,
  };
}

class TicketInfo extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      ticketId: this.props.match.params.id,
      tabValue: 0,
      ticket: {},
      ticketHistory: [],
      ticketComments: [],
      ticketAttachments: [],
      selectedFile: null,
      status: '',
      comment: '',
      addCommentInfo: '',
      updateTicketAccessError: "",
      createCommentError: [],
      isInvalidComment: false,
      changeStatusError: []
    }

    this.editTicket = this.editTicket.bind(this);
    this.handleAttachmentChange = this.handleAttachmentChange.bind(this);
    this.handleEnterComment = this.handleEnterComment.bind(this);
    this.downloadFileById = this.downloadFileById.bind(this);
    this.addComment = this.addComment.bind(this);
    this.handleCancelTicket = this.handleCancelTicket.bind(this);
    this.handleSubmitTicket = this.handleSubmitTicket.bind(this);
  }

  componentDidMount() {
    const ticketId = this.state.ticketId;

    TicketService.getTicketById(ticketId).then(res => {
      this.setState({ ticket: res.data });
      this.setState({ status: res.data.status })
    })

    AttachmentService.getAllAttachmentsByTicketId(ticketId).then(res => {
      this.setState({ ticketAttachments: res.data });
    });

    HistoryService.getLastFiveHistoryByTicketId(ticketId).then(res => {
      this.setState({ ticketHistory: res.data });
    });

    CommentService.getLastFiveCommentsByTicketId(ticketId).then(res => {
      this.setState({ ticketComments: res.data });
    });
  }

  editTicket() {
    const {ticket, ticketId } = this.state;
    const userRole = sessionStorage.getItem("userRole");

    if (userRole !== "ROLE_ENGINEER" && ticket.status === 'DRAFT') {
      this.props.history.push(`/update-ticket/${ticketId}`);
    } else {
      this.setState({ updateTicketAccessError: "You can't have access to update ticket" });
    }
  }

  handleAttachmentChange = (event) => {
    this.setState({
      selectedFile: event.target.files[0]
    });
  }

  handleEnterComment = (event) => {
    this.setState({
      comment: event.target.value,
    });
  }

  handleTabChange = (event, value) => {
    this.setState({
      tabValue: value,
    });
  }

  downloadFileById = (attachmentId, fileName) => {
    const ticketId = this.state.ticketId;

    AttachmentService.getAttachmentById(attachmentId, ticketId).then(res => {
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");

      link.href = url;
      link.setAttribute(
        "download",
        fileName
      );

      document.body.appendChild(link);

      link.click();

      link.parentNode.removeChild(link);
    });
  }

  addComment = () => {
    const ticketId = this.state.ticketId;

    const comment = {
      text: this.state.comment
    }

    CommentService.createComment(comment, ticketId).then(res => {
      this.setState({ addCommentInfo: `New commit has just been added to ticket ${ticketId} : ${comment.text}`});
      this.setState({ isInvalidComment: false });
    }).catch(err => {
      if(err.response.status === 400) {
        this.setState({ isInvalidComment: true });
        this.setState({ createCommentError: err.response.data })
        this.setState({ addCommentInfo: '' })
      }
    })
  }

  handleShowAllHistory = (event) => {
    const ticketId = this.state.ticketId;

    HistoryService.getAllHistoryByTicketId(ticketId).then((res) => {
      this.setState({ ticketHistory: res.data })
    });
  }

  handleShowAllComments = (event) => {
    const ticketId = this.state.ticketId;

    CommentService.getAllCommentsByTicketId(ticketId).then((res) => {
      this.setState({ ticketComments: res.data })
    });
  }

  handleCancelTicket = (previousStatus) => {
    const ticketId = this.state.ticketId;

    TicketService.changeTicketStatusToCanceled(ticketId).then((res) => {
      if (res.status === 201) {
        this.setState({ status: 'CANCELED'});
        this.props.history.push(`/tickets`);

      }
    });
  }

  handleSubmitTicket = (previousStatus) => {
    const ticketId = this.state.ticketId;

    TicketService.changeTicketStatusToNew(ticketId).then((res) => {
      if (res.status === 201) {
        this.setState({ status: 'NEW' });
        this.props.history.push(`/tickets`);
      }
    });
  }

  render() {
    const { ticket, status, comment, tabValue, ticketAttachments, ticketComments, ticketHistory,
            updateTicketAccessError, addCommentInfo, isInvalidComment, createCommentError } = this.state;
    const { url } = this.props.match;
    const { editTicket, handleCancelTicket, handleSubmitTicket, handleShowAllHistory, handleShowAllComments,
            downloadFileById, addComment, handleTabChange } = this;

    return (
      <Switch>
        <Route exact path={url}>
          <div align='center'>
            {updateTicketAccessError &&
              <Typography className="has-error" component="h6" variant="h5">
                {updateTicketAccessError}
              </Typography>
            }
          </div>
          <div className="ticket-data-container">
            <div className={"buttons-container"}>
              <Button component={Link} to="/tickets" variant="contained" color="secondary">
                Ticket list
              </Button>
              <Button onClick={() => editTicket(ticket.id)}
                variant="contained"
                color="primary">
                Edit
              </Button>
              <Button component={Link} to={`/feedbacks/${ticket.id}`} variant="contained" color="secondary">
                Leave Feedback
              </Button>
            </div>
            <div className="ticket-data-container__title">
              <Typography variant="h4">{`Ticket № ${ticket.id} - ${ticket.name}`}</Typography>
            </div>
            <div className="ticket-data-container__info">
              <TableContainer className="ticket-table" component={Paper}>
                <Table>
                  <TableBody>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Created on:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.createdOn}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Category:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.category}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Status:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.status}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Urgency:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.urgency}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Desired Resolution Date:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.desiredResolutionDate}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Owner:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.ticketOwner}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Approver:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.approver || "Not assigned"}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Assignee:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.assignee || "Not assigned"}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Attachments:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticketAttachments.map((item, index) => {
                            return (
                              <TableRow key={index}>
                                <Link
                                  to={`${url}/attachments/${item.id}`}
                                  target="_blank"
                                  onClick = {() => downloadFileById(item.id, item.name)}
                                >
                                  <table>
                                    <tr>
                                      <td className="link">
                                        {index + 1 + ". "}
                                        {item.name}
                                      </td>
                                    </tr>
                                  </table>
                                </Link>
                              </TableRow>
                            );
                          })}
                        </Typography>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          Description:
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography align="left" variant="subtitle1">
                          {ticket.description || "Not assigned"}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </TableContainer>
            </div>
            {status === "DRAFT" && (
              <div className="ticket-data-container__button-section">
                <ButtonGroup variant="contained">
                  <Button
                    color="secondary"
                    onClick={() => handleSubmitTicket(status)}
                  >
                    Submit
                  </Button>
                  <Button
                    onClick={() => handleCancelTicket(status)}
                  >
                    Cancel
                  </Button>
                </ButtonGroup>
              </div>
            )}
            <div className="ticket-data-container__comments-section comments-section">
              <div className="">
                <Tabs
                  variant="fullWidth"
                  onChange={handleTabChange}
                  value={tabValue}
                  indicatorColor="primary"
                  textColor="primary"
                >
                  <Tab label="History" {...a11yProps(0)} />
                  <Tab label="Comments" {...a11yProps(1)} />
                </Tabs>
                <TabPanel value={tabValue} index={0}>
                  <HistoryTable
                    history={ticketHistory}
                    showAllHistoryCallback={handleShowAllHistory} />
                </TabPanel>
                <TabPanel value={tabValue} index={1}>
                  <CommentsTable
                    comments={ticketComments}
                    showAllCommentsCallback={handleShowAllComments} />
                </TabPanel>
              </div>
            </div>
            {tabValue && (
              <div className="ticket-data-container__enter-comment-section enter-comment-section">
                <div>
                  {addCommentInfo &&
                    <Typography className="has-error" component="h6" variant="h5" align='center'>
                      {addCommentInfo}
                    </Typography>
                  }
                  {isInvalidComment &&
                    <Typography className="has-error" component="h6" variant="h5">
                      {createCommentError.map((error) => (
                        <div align='center'>
                          {error.text}
                        </div>
                      ))}
                    </Typography>
                  }
                </div>
                <TextField
                  label="Enter a comment"
                  multiline
                  rows={4}
                  value={comment}
                  variant="filled"
                  className="comment-text-field"
                  onChange={this.handleEnterComment}
                />
                <div className="enter-comment-section__add-comment-button">
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={addComment}
                  >
                    Add Comment
                  </Button>
                </div>
              </div>
            )}
          </div>
        </Route>
      </Switch>
    );
  }
}

TicketInfo.propTypes = {
  match: PropTypes.object,
};

const TicketInfoWithRouter = withRouter(TicketInfo);
export default TicketInfoWithRouter;
