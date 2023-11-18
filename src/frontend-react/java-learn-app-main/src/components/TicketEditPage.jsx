import React from "react";
import {
  Button,
  InputLabel,
  FormControl,
  MenuItem,
  Select,
  TextField,
  Typography,
  Link,
  TableRow,

} from "@material-ui/core";
import { withRouter } from "react-router-dom";
import { CATEGORIES_OPTIONS, URGENCY_OPTIONS } from "../constants/inputsValues";
import TicketService from '../services/TicketService';
import CommentService from '../services/CommentService';
import AttachmentService from "../services/AttachmentService";

class TicketEditPage extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      ticketId: this.props.match.params.id,
      ticket: {},
      category: '',
      name: '',
      description: '',
      urgency: '',
      desiredResolutionDate: '',
      ticketAttachments: [],
      attachmentUploadError: [],
      isInvalidAttachment: false,
      isFileSelected: true,
      comment: '',
      isInvalidTicket: false,
      addCommentInfo: '',
      createCommentError: [],
      isInvalidComment: false,
      updateTicketError: [],
    };

    this.handleCategoryChange = this.handleCategoryChange.bind(this);
    this.handleNameChange = this.handleNameChange.bind(this);
    this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
    this.handleUrgencyChange = this.handleUrgencyChange.bind(this);
    this.handleDesiredResolutionDate = this.handleDesiredResolutionDate.bind(this);
    this.handleCommentChange = this.handleCommentChange.bind(this);
    this.handleAttachmentChange = this.handleAttachmentChange.bind(this);
    this.handleSaveDraft = this.handleSaveDraft.bind(this);
    this.handleSubmitTicket = this.handleSubmitTicket.bind(this);
    this.viewTicket = this.viewTicket.bind(this);
  }

  componentDidMount() {
    const ticketId = this.state.ticketId;

    TicketService.getTicketById(ticketId).then((res) => {
      const ticket = res.data;

      this.setState({
        category: ticket.category,
        name: ticket.name,
        description: ticket.description,
        urgency: ticket.urgency,
        desiredResolutionDate: ticket.desiredResolutionDate,
      });
    });

    AttachmentService.getAllAttachmentsByTicketId(ticketId).then(res => {
      this.setState({ ticketAttachments: res.data });
    });
  }

  handleCategoryChange = (event) => {
    this.setState({
      category: event.target.value,
    });
  };

  handleNameChange = (event) => {
    this.setState({
      name: event.target.value,
    });
  };

  handleDescriptionChange = (event) => {
    this.setState({
      description: event.target.value,
    });
  };

  handleUrgencyChange = (event) => {
    this.setState({
      urgency: event.target.value,
    });
  };

  handleDesiredResolutionDate = (event) => {
    this.setState({
      desiredResolutionDate: event.target.value,
    });
  };

  handleCommentChange = (event) => {
    this.setState({
      comment: event.target.value,
    });
  };

  handleAttachmentChange = (event) => {
    this.setState({
      selectedFile: event.target.files[0]
    });
  };

  addFile = (event) => {
    const { ticketId, selectedFile } = this.state;
    const attachment = new FormData();

    attachment.append(
      "file", selectedFile,
    );

    if (selectedFile != null) {
      AttachmentService.uploadAttachment(attachment, ticketId).then(res => {
        this.setState({ ticketAttachments: res.data });
        this.setState({ attachmentUploadError: [] });
        this.setState({ isFileSelected: true });
      }).catch(err => {
        if (err.response.status === 400) {
          this.setState({ isInvalidAttachment: true });
          this.setState({ attachmentUploadError: err.response.data });
          this.setState({ isFileSelected: true });
        }
        if (err.response.status === 409) {
          this.setState({ isInvalidAttachment: true });
          this.setState({ attachmentUploadError: err.response.data.info });
          this.setState({ isFileSelected: true });
        }
      })
    } else {
      this.setState({ isFileSelected: false });
      this.setState({ attachmentUploadError: [] });
    }
  }

  deleteFile = (event) => {
    const {ticketId, selectedFile} = this.state;

    if (selectedFile != null) {
      AttachmentService.deleteAttachment(selectedFile.name, ticketId).then(res => {
        this.setState({ attachmentUploadError: [] });
        this.setState({ ticketAttachments: res.data });
        this.setState({ isFileSelected: true });
      }).catch(err => {
        this.setState({ isInvalidAttachment: true });
        this.setState({ attachmentUploadError: err.response.data.info });
        this.setState({ isFileSelected: true });
      })
    } else {
      this.setState({ isFileSelected: false });
      this.setState({ attachmentUploadError: [] });
    }
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

  addComment = (event) => {
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

  handleSaveDraft = () => {
    const ticketId = this.state.ticketId;

    const ticket = {
      category: this.state.category,
      name: this.state.name,
      description: this.state.description,
      urgency: this.state.urgency,
      desiredResolutionDate: this.state.desiredResolutionDate,
    };

    TicketService.updateTicketAndSaveAsDraft(ticket, ticketId).then(res => {
      this.setState({ updateTicketError: [] })
      this.props.history.push(`/tickets`);
    }).catch(err => {
      if (err.response.status === 400) {
        this.setState({ isInvalidTicket: true });
        this.setState({ updateTicketError: err.response.data })
      }
    })
  }

  handleSubmitTicket = () => {
    const ticketId = this.state.ticketId;

    const ticket = {
      category: this.state.category,
      name: this.state.name,
      description: this.state.description,
      urgency: this.state.urgency,
      desiredResolutionDate: this.state.desiredResolutionDate,
    };

    TicketService.changeTicketStatusToNew(ticketId).then((res) => {
    });

    TicketService.updateTicket(ticket, ticketId).then(res => {
      this.setState({ updateTicketError: [] });
      this.props.history.push(`/tickets`);
    }).catch(err => {
      if (err.response.status === 400) {
        this.setState({ isInvalidTicket: true });
        this.setState({ updateTicketError: err.response.data })
      }
    });
  }

  viewTicket(id) {
    this.props.history.push(`/tickets/${id}`);
  }

  render() {
    const { ticketId, name, category, comment, description, desiredResolutionDate, urgency,
            ticketAttachments, attachmentUploadError, isInvalidAttachment, isFileSelected, addCommentInfo,
             isInvalidComment, createCommentError, isInvalidTicket, updateTicketError} = this.state;
    const { url } = this.props.match;
    const { handleCategoryChange, handleNameChange, handleUrgencyChange, handleDescriptionChange,
            handleDesiredResolutionDate, handleCommentChange, handleAttachmentChange, handleSaveDraft,
            handleSubmitTicket, addFile, deleteFile, downloadFileById, addComment, viewTicket } = this;

    return (
      <div className="ticket-creation-form-container" >
        <div className={"buttons-container"}>
          <Button onClick={() => viewTicket(ticketId)} variant="contained" color="secondary">
            Ticket Overview
          </Button>
        </div>
        <div className="ticket-creation-form-container__title">
          <Typography variant="h4">{`Edit Ticket №${ticketId}`}</Typography>
        </div>
       {isInvalidTicket &&
          <Typography className="has-error" component="h6" variant="h5" align="center">
            {updateTicketError.map((error) => (
              <div>
                <ul>
                  <li>{error.name}</li>
                  <li>{error.description}</li>
                  <li>{error.desiredResolutionDate}</li>
                </ul>
              </div>
            ))}
          </Typography>
        }
        <div className="ticket-creation-form-container__form">
          <div className="inputs-section">
            <div className="ticket-creation-form-container__inputs-section inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl>
                <TextField
                  required
                  label="Name"
                  variant="outlined"
                  onChange={handleNameChange}
                  id="name-label"
                  value={name}
                />
              </FormControl>
            </div>
            <div className="inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl variant="outlined" required>
                <InputLabel shrink htmlFor="category-label">
                  Category
                </InputLabel>
                <Select
                  value={category}
                  label="Category"
                  onChange={handleCategoryChange}
                  inputProps={{
                    name: "category",
                    id: "category-label",
                  }}
                >
                  {CATEGORIES_OPTIONS.map((item, index) => {
                    return (
                      <MenuItem value={item.value} key={index}>
                        {item.label}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
            </div>
            <div className="inputs-section__ticket-creation-input ticket-creation-input">
              <FormControl variant="outlined" required>
                <InputLabel shrink htmlFor="urgency-label">
                  Urgency
                </InputLabel>
                <Select
                  value={urgency}
                  label="Urgency"
                  onChange={handleUrgencyChange}
                  className={"ticket-creation-input_width200"}
                  inputProps={{
                    name: "urgency",
                    id: "urgency-label",
                  }}
                >
                  {URGENCY_OPTIONS.map((item, index) => {
                    return (
                      <MenuItem value={item.value} key={index}>
                        {item.label}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
            </div>
          </div>
          <div className="inputs-section-attachment">
            <div className="inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl>
                <InputLabel shrink htmlFor="urgency-label">
                  Desired resolution date
                </InputLabel>
                <TextField
                  onChange={handleDesiredResolutionDate}
                  label="Desired resolution date"
                  type="date"
                  id="resolution-date"
                  value={desiredResolutionDate}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </FormControl>
            </div>
            <div className="ticket-creation-input">
              <FormControl>
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
                <table>
                  <Typography variant="caption">Select attachment</Typography>
                    <tr className="table">
                      <td>
                        <input
                          className="input"
                          type="file"
                          onChange={handleAttachmentChange}
                        />
                      </td>
                    </tr>
                    <tr className="table">
                      <td>
                        <button
                          onClick={(e) => addFile(e)}
                        >
                          Add File
                        </button>
                        <button
                          onClick={(e) => deleteFile(e)}
                        >
                          Delete File
                        </button>
                      </td>
                    </tr>
                  </table>
                </FormControl>
              </div>
            </div>
            {isInvalidAttachment &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                {attachmentUploadError}
              </Typography>
            }
            {!isFileSelected &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                You should select the file first
              </Typography>
            }
            <div className="inputs-section">
              <FormControl>
                <TextField
                  label="Description"
                  multiline
                  rows={4}
                variant="outlined"
                value={description}
                className="creation-text-field creation-text-field_width680"
                onChange={handleDescriptionChange}
              />
            </FormControl>
          </div>
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
          <div className="inputs-section">
            <FormControl>
              <TextField
                label="Comment"
                multiline
                rows={4}
                variant="outlined"
                value={comment}
                className="creation-text-field creation-text-field_width680"
                onChange={handleCommentChange}
              />
            </FormControl>
          </div>
          <section className="submit-button-section">
            <Button
              variant="contained"
              onClick={(e) => addComment(e)}
            >
              Add Comment
            </Button>
            <Button variant="contained" onClick={handleSaveDraft}>
              Save as Draft
            </Button>
            <Button
              variant="contained" onClick={handleSubmitTicket} color="primary">
              Submit
            </Button>
          </section>
        </div>
      </div>
    );
  }
}

const TicketEditPageWithRouter = withRouter(TicketEditPage);
export default TicketEditPageWithRouter;